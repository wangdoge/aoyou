package com.usercenter.common;

import lombok.Data;

/**
 * Date 2023/5/31 20:59
 * author:wyf
 * 通用分页请求参数
 */
@Data
public class PageRequest {

    protected int pageSize=10;

    protected int pageNum=1;
}
