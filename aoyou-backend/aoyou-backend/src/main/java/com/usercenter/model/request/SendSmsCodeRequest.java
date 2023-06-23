package com.usercenter.model.request;

import lombok.Data;

/**
 * Date 2023/6/23 13:42
 * author:wyf
 */
@Data
public class SendSmsCodeRequest {
    private String phoneNumber;
    private int min=5;
}
