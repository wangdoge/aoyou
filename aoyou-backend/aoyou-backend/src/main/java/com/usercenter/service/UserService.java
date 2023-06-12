package com.usercenter.service;

import com.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.usercenter.model.request.UserAddRequest;
import com.usercenter.model.request.UserModifyPasswordRequest;
import com.usercenter.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author wang
* @description 针对表【user】的数据库操作Service1
* @createDate 2023-05-08 22:24:15
*/
public interface UserService extends IService<User> {



    /**
     * 用户注册
     * @param userAccount 账号
     * @param userPassword 密码
     * @param checkPassword 校验密码
     * @return 新用户的id
     */
    long userRegister(String userAccount,String userPassword,String checkPassword,String username,String planetCode);

    /**
     *
     * @param userAccount 账号
     * @param userPassword 密码
     * @return 用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     * @param user
     * @return
     */
    User getSafeUser(User user);

    /**
     * 用户注销
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);

    /**
     * 添加新用户
     * @param addUser
     * @return
     */

    int addUser(UserAddRequest addUser);

    /**
     * 校验密码是否一致
     * @param request
     * @return
     */
    boolean checkPassword(UserModifyPasswordRequest passwordRequest,HttpServletRequest request);

    /**
     * 获取现在登录的用户
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 按标签查找用户
     * @param tagList
     * @param loginUser
     * @return
     */
    List<User> searchByTags(List<String> tagList,User loginUser);

    /**
     * 更新用户
     * @param user
     * @return
     */
    Integer updateUser(User user, User loginUser);

    /**
     * 是否为管理员
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);
    /**
     * 是否为管理员
     * @param user
     * @return
     */
    boolean isAdmin(User user);

    /**
     * 匹配用户
     * @param num
     * @param loginUser
     * @return
     */
    List<User> matchUsers(Long num, User loginUser);

}
