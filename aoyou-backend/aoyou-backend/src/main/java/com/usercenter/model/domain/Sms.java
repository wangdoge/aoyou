package com.usercenter.model.domain;

import lombok.Data;

/**
 * Date 2023/6/23 13:42
 * author:wyf
 */
@Data
public class Sms {
    private String phoneNumber;
    private String code;
    private int min;
}
