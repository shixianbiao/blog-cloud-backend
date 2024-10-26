package com.xb.blog.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xb.blog.common.core.utils.UserUtil;
import com.xb.blog.web.dao.FollowDao;
import com.xb.blog.web.entity.FollowEntity;
import com.xb.blog.web.publisher.MessagePublisher;
import com.xb.blog.web.service.FollowService;
import com.xb.blog.web.vo.FollowSaveVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FollowServiceImpl extends ServiceImpl<FollowDao, FollowEntity> implements FollowService {

    @Autowired
    private MessagePublisher messagePublisher;

    @Override
    public void save(FollowSaveVo vo) {
        Boolean isFollow = vo.getIsFollow();
        if (isFollow) {
            FollowEntity entity = new FollowEntity();
            entity.setUserId(UserUtil.getUserId());
            entity.setTargetUserId(vo.getTargetUserId());
            save(entity);
            //发送消息
            messagePublisher.sendMessage(4, null, UserUtil.getUserId(), vo.getTargetUserId(), null, null);
        } else {
            remove(new QueryWrapper<FollowEntity>().eq("user_id", UserUtil.getUserId()).eq("target_user_id", vo.getTargetUserId()));
        }
    }
}