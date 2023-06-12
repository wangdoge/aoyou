package com.usercenter.model.vo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * @TableName user
 */
@TableName(value ="user")
@Data
public class UserVO implements Serializable{
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 电话
     */
    private String phone;

    /**
     *
     */
    private String email;

    /**
     * 状态(0=正常)
     */
    private Integer userStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    /**
     * 编号
     */
    private String planetCode;

    /**
     * 用户角色 0-普通 1-管理员
     */
    private Integer userRole;

    /**
     * 用户标签
     */
    private String tags;

    /**
     * 用户描述
     */
    private String profile;


    @TableField(exist = false)
    private static final long serialVersionUID = 12L;
}