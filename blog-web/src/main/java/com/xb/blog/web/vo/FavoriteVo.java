package com.xb.blog.web.vo;

import lombok.Data;

/**
 * 收藏夹数据vo
 */
@Data
public class FavoriteVo {
    private String uid;
    private String name;
    private Boolean isDefault;
}
