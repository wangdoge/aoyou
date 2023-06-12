package com.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户退出请求
 * @TableName team
 */

@Data
public class TeamDeleteRequest implements Serializable {
    /**
     * 队伍id
     */
    private Long teamId;
}