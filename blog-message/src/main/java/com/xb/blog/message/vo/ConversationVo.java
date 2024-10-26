package com.xb.blog.message.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ConversationVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 私信表唯一id
     */
    private String id;
    /**
     * 联系人唯一id
     */
    private String contactId;
    /**
     * 未查收的消息条数
     */
    private Integer notReceiveCount;
    /**
     * 联系人昵称
     */
    private String contactNickName;
    /**
     * 用户头像id
     */
    private String picId;
    /**
     * 最新一条消息内容
     */
    private String chatContent;
}