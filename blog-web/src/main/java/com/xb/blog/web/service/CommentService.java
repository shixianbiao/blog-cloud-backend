package com.xb.blog.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xb.blog.web.entity.CommentEntity;
import com.xb.blog.web.vo.CommentSaveVo;
import com.xb.blog.web.vo.CommentVo;

public interface CommentService extends IService<CommentEntity> {
    /**
     * 根据博客id获取评论数据
     *
     * @param id
     * @return
     */
    CommentVo getTreeDataById(String id, Long page);

    /**
     * 修改点赞数
     *
     * @param commentId
     * @param count
     * @return
     */
    Long updateLikeCount(String commentId, Long count);

    /**
     * 保存评论
     *
     * @param vo
     * @return
     */
    void save(CommentSaveVo vo);
}
