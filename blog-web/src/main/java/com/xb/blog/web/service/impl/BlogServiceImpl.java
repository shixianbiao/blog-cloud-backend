package com.xb.blog.web.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xb.blog.web.common.utils.UserUtil;
import com.xb.blog.web.dao.BlogDao;
import com.xb.blog.web.entity.BlogEntity;
import com.xb.blog.web.service.BlogService;
import com.xb.blog.web.service.BlogTagService;
import com.xb.blog.web.service.DraftService;
import com.xb.blog.web.vo.BlogEditorVo;
import com.xb.blog.web.vo.BlogListVo;
import com.xb.blog.web.vo.BlogPreviewVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
public class BlogServiceImpl extends ServiceImpl<BlogDao, BlogEntity> implements BlogService {

    @Autowired
    private DraftService draftService;

    @Autowired
    private BlogTagService blogTagService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 发布博客
     *
     * @param vo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(BlogEditorVo vo) {
        //保存博客表
        BlogEntity blog = new BlogEntity();
        BeanUtil.copyProperties(vo, blog);
        blog.setStatus(1);
        blog.setAuthor(UserUtil.getUserId());
        saveOrUpdate(blog);

        //保存标签绑定数据
        blogTagService.save(vo.getUid(), vo.getTagUids());

        //删除草稿表
        draftService.removeById(vo.getUid());

    }

    /**
     * 查询博客列表
     *
     * @return
     */
    @Override
    public List<BlogListVo> listBlog(Long page) {
        //处理特殊情况
        if (page == null) page = 1L;

        //定义缓存Keu格式（每页数据单独缓存，且固定每页条数为10条）
        String lockKey = "HOME_BLOG_LIST_LOCK_SIZE_10_PAGE_" + page;
        String dataKey = "HOME_BLOG_LIST_DATA_SIZE_10_PAGE_" + page;

        //换算分页参数（使用OFFSET关键字进行分页，故此处起始页码应为0）
        page = (page - 1L) * 10L;

        //从缓存中获取数据
        String cache = redisTemplate.opsForValue().get(dataKey);
        if (StrUtil.isNotBlank(cache)) {
            //缓存中有数据 直接返回
            return JSONUtil.toList(cache, BlogListVo.class);
        }

        //获取分布式锁
        String uuid = UUID.randomUUID().toString();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent(lockKey, uuid);

        if (lock) {
            //拿到分布式锁，查询数据库
            try {
                List<BlogListVo> list = baseMapper.listBlog(page);
                if (CollUtil.isEmpty(list)) {
                    //设置空值 避免缓存穿透
                    redisTemplate.opsForValue().set(dataKey, JSONUtil.toJsonStr(list), 10, TimeUnit.SECONDS);
                } else {
                    redisTemplate.opsForValue().set(dataKey, JSONUtil.toJsonStr(list), 10, TimeUnit.MINUTES);
                }
                return list;
            } finally {
                //使用lua脚本 保证释放分布式锁的原子性
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                redisTemplate.execute(new DefaultRedisScript<>(script, Long.class)
                        , Arrays.asList(lockKey), uuid);
            }
        }

        //未拿到分布式锁，进行一定次数的重试
        for (int i = 0; i < 3; i++) {
            try {
                //适当休眠 避免cpu空转
                Thread.sleep(200);

                //再次查询缓存
                cache = redisTemplate.opsForValue().get(dataKey);
                if (StrUtil.isNotBlank(cache)) {
                    //缓存中有数据 直接返回
                    return JSONUtil.toList(cache, BlogListVo.class);
                }

            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        }

        //重试获取数据失败 返回空集合
        return Collections.emptyList();
    }

    /**
     * 根据id获取博客预览数据
     *
     * @param id
     * @return
     */
    @Override
    public BlogPreviewVo getBlogPreviewById(String id) {
        return baseMapper.getBlogPreviewById(id, UserUtil.getUserId());
    }

    /**
     * 修改点赞数
     *
     * @param blogId 博客id
     * @param count  点赞数 1/-1
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Long updateLikeCount(String blogId, Long count) {
        baseMapper.updateLikeCount(blogId, count);
        return baseMapper.getLikeCountByBlogId(blogId);
    }

    /**
     * 修改评论数
     *
     * @param blogId
     * @param count
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void updateCommentCount(String blogId, Long count) {
        baseMapper.updateCommentCount(blogId, count);
    }

    /**
     * 根据博客id获取博客的评论数
     *
     * @param blogId
     * @return
     */
    @Override
    public Long getCommentCount(String blogId) {
        return baseMapper.getCommentCountByBlogId(blogId);
    }

    /**
     * 根据博客id修改博客的点击量
     * 因为修改点击量的场景是在预览文章时，所以这里采用异步调用的方式，不影响文章数据的返回
     *
     * @param blogId
     * @param count
     */
    @Async
    @Override
    public void updateClickCount(String blogId, Long count) {
        baseMapper.updateClickCount(blogId, count);
    }

    /**
     * 修改博客收藏数 并返回最新收藏数
     *
     * @param blogId
     * @param count
     * @return
     */
    @Override
    public Long updateCollectCount(String blogId, Long count) {
        baseMapper.updateCollectCount(blogId, count);
        return baseMapper.getCollectCountByBlogId(blogId);
    }
}