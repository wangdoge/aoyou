package com.usercenter.model.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Date 2023/6/1 13:48
 * author:wyf
 */
@Data
public class TeamNameVO {

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
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 入队用户列表
     */
    List<UserVO> userList;

    /**
     * 创建人用户信息
     */
    UserVO createUser;

    /**
     * 用户是否已加入
     */
    private boolean hasJoin =false;

    /**
     * 加入的用户数量
     */
    private Integer hasJoinNum;
}
