package com.usercenter.model.request;

import lombok.Data;

/**
 * Date 2023/5/18 22:06
 * author:wyf
 */
@Data
public class UserModifyPasswordRequest {
    private String password;
    private String newPassword;

}
