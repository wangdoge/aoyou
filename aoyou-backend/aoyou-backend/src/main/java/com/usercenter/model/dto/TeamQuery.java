package com.usercenter.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.usercenter.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * Date 2023/5/31 20:43
 * author:wyf
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TeamQuery extends PageRequest {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * id列表
     */
    private List<Long> idList;

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
     * 搜索关键词：同时对队伍名称和描述搜索
     */
    private String searchText;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
