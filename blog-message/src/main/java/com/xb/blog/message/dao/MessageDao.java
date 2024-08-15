package com.xb.blog.message.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xb.blog.message.entity.MessageEntity;
import com.xb.blog.message.vo.MessageVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageDao extends BaseMapper<MessageEntity> {
    /**
     * 根据消息id判断消息是否已存在
     *
     * @param uid
     * @return
     */
    Boolean exist(@Param("uid") String uid);

    /**
     * 根据用户id获取未读消息数量
     *
     * @param uid
     * @return
     */
    List<Long> getMessageCount(@Param("uid") String uid);

    /**
     * 获取消息列表
     *
     * @param type
     * @param page
     * @return
     */
    List<MessageVo> list(@Param("type") int type, @Param("page") Long page, @Param("receiveUserUid") String receiveUserUid);

    /**
     * 获取所有消息未接收的条数
     *
     * @param userId
     * @return
     */
    List<Long> counts(@Param("userUid") String userId);

    /**
     * 将某用户的某一类未读消息变为已读
     *
     * @param type
     * @param userId
     */
    void updateMessageToReceived(@Param("type") int type, @Param("userUid") String userId);
}
