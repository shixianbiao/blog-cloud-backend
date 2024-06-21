package com.xb.blog.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 标签表
 * 
 * @author shixianbiao
 * @email sxb0326@qq.com
 * @date 2024-04-07 22:57:53
 */
@Data
@TableName("t_tag")
public class TagEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId(type = IdType.ASSIGN_UUID)
	private String uid;
	/**
	 * 标签内容
	 */
	private String content;
	/**
	 * 逻辑删除 1：不删除，0：删除
	 */
	private Integer status;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 修改时间
	 */
	private Date updateTime;
	/**
	 * 排序字段
	 */
	private Integer sort;

}
