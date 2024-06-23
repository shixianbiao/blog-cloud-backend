package com.xb.blog.web.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 博客预览时的数据Vo
 */
@Data
public class BlogPreviewVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 唯一id
     */
    private String uid;
    /**
     * 博客标题
     */
    private String title;
    /**
     * 博客内容
     */
    private String content;
    /**
     * 作者id
     */
    private String authorId;
    /**
     * 作者昵称
     */
    private String authorName;
    /**
     * 文章点击量
     */
    private Long clickCount;
    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createTime;
}