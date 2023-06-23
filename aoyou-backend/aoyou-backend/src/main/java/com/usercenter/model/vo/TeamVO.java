package com.usercenter.model.vo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 
 * @TableName team
 */
@TableName(value ="team")
@Data
public class TeamVO implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 队伍头像
     */
    private String teamAvatar;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 队长id
     */
    private Long userId;

    /**
     * 0-公开 1-私有 2-加密
     */
    private Integer status;

    /**
     * 入队用户列表
     */
    List<UserVO> userList;

    /**
     * 入队用户数量
     */
    int userCount;

    /**
     * 用户是否已加入
     */
    private boolean hasJoin =false;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}