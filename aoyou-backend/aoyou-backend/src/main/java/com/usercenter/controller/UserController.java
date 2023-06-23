package com.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usercenter.common.BaseResponse;
import com.usercenter.common.ErrorCode;
import com.usercenter.common.ResultUtils;
import com.usercenter.exception.BusinessException;
import com.usercenter.model.domain.User;
import com.usercenter.model.request.*;
import com.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.usercenter.contant.UserConstant.USER_LOGIN_STATE;

/**
 * Date 2023/5/10 19:01
 * author:wyf
 */
@RestController
@RequestMapping("/user")
@CrossOrigin()
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest,HttpServletRequest request){
        if(userRegisterRequest==null){
//            return ResultUtils.error(ErrorCode.PARAM_ERROR);
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        String username = userRegisterRequest.getUsername();
        String userAccount = userRegisterRequest.getUserAccount();
        String password = userRegisterRequest.getPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        Boolean autoLogin = userRegisterRequest.getAutoLogin();
        if(StringUtils.isAnyBlank(userAccount,password,checkPassword)){
            return null;
        }
        Long result = userService.userRegister(userAccount, password, checkPassword, username, planetCode,autoLogin,request);
        return ResultUtils.success(result);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request){
        if(request==null){
            return null;
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLoginByPwd(@RequestBody UserLoginByPwdRequest userLoginByPwdRequest, HttpServletRequest request){
        if(userLoginByPwdRequest ==null){
            return null;
        }
        String userAccount = userLoginByPwdRequest.getUserAccount();
        String password = userLoginByPwdRequest.getPassword();
        if(StringUtils.isAnyBlank(userAccount,password)){
            return null;
        }
        User user = userService.userLoginByPwd(userAccount, password, request);
        User safeUser = userService.getSafeUser(user);
        return ResultUtils.success(safeUser);
    }

    @PostMapping("/login/phone")
    public BaseResponse<User> userLoginByPhone(@RequestBody UserLoginByPhoneRequest userLoginByPhoneRequest, HttpServletRequest request){
        if(userLoginByPhoneRequest ==null){
            return null;
        }
        String userAccount = userLoginByPhoneRequest.getUserAccount();
        String code = userLoginByPhoneRequest.getCode();

        if(StringUtils.isAnyBlank(userAccount,code)){
            return null;
        }
        User user = userService.userLoginByPhone(userAccount, code, request);
        User safeUser = userService.getSafeUser(user);
        return ResultUtils.success(safeUser);
    }

    @GetMapping("/recommend")
    public BaseResponse<Page<User>> recommendUsers(int pageSize,int pageNum,HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        //如果有缓存,直接查缓存
        String redisKey=String.format("aoyou:user:recommend:%s:%s",loginUser.getId(),pageNum);
        Page<User> userPage = (Page<User>)opsForValue.get(redisKey);
        if(userPage!=null){
            return ResultUtils.success(userPage);
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        userPage = userService.page(new Page<>(pageNum,pageSize),queryWrapper);
        try {
            opsForValue.set(redisKey,userPage,1, TimeUnit.DAYS);
        }catch (Exception e){
            log.error("redis error:",e);
        }

        return ResultUtils.success(userPage);
    }


    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username,HttpServletRequest request){
        //仅管理员查询
        if(!userService.isAdmin(request)){
            return null;
        }

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNoneBlank(username)){
            queryWrapper.like(User::getUsername,username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> result = userList.stream().map(user -> userService.getSafeUser(user)).collect(Collectors.toList());
        return ResultUtils.success(result);
    }


    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody UserDeleteRequest deleteRequest, HttpServletRequest request){
        if(!userService.isAdmin(request)){
            return null;
        }
        boolean result = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(result);
    }

    @GetMapping("/search2")
    public BaseResponse<List<User>> searchUsers(UserSearchRequest searchRequest, HttpServletRequest request) {
        // 管理员校验
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权限");
        }
        String username = searchRequest.getUsername();
        String userAccount = searchRequest.getUserAccount();
        Integer gender = searchRequest.getGender();
        String phone = searchRequest.getPhone();
        String email = searchRequest.getEmail();
        Integer userStatus = searchRequest.getUserStatus();
        Integer userRole = searchRequest.getUserRole();
        String userCode = (String) searchRequest.getUserCode();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        Date updateTime = searchRequest.getUpdateTime();
        Date createTime = searchRequest.getCreateTime();
        // username
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        // userAccount
        if (StringUtils.isNotBlank(userAccount)) {
            queryWrapper.like("userAccount", userAccount);
        }
        // gender
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.eq("gender", gender);
        }
        // phone
        if (StringUtils.isNotBlank(phone)) {
            queryWrapper.like("phone", phone);
        }
        // email
        if (StringUtils.isNotBlank(email)) {
            queryWrapper.like("email", email);
        }
        // userStatus
        if (userStatus != null) {
            queryWrapper.eq("userStatus", userStatus);
        }

        if (userRole != null) {
            queryWrapper.eq("userRole", userRole);
        }

        if (StringUtils.isNotBlank(userCode)) {
            queryWrapper.eq("plantCode", userCode);
        }

        if (updateTime != null) {
            queryWrapper.like("updateTime", updateTime);
        }
        if (createTime != null) {
            queryWrapper.like("createTime", createTime);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> users = userList.stream().map(userService::getSafeUser).collect(Collectors.toList());
        return ResultUtils.success(users);
    }

    @PostMapping("/search3")
    public BaseResponse<List<User>> searchUsers2(@RequestBody UserSearchRequest searchRequest, HttpServletRequest request) {
        // 管理员校验
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权限");
        }
        String username = searchRequest.getUsername();
        String userAccount = searchRequest.getUserAccount();
        Integer gender = searchRequest.getGender();
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        // username
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like(User::getUsername, username);
            List<User> userList = userService.list(queryWrapper);
            List<User> users = userList.stream().map(userService::getSafeUser).collect(Collectors.toList());
            return ResultUtils.success(users);
        }else if(StringUtils.isNotBlank(userAccount)){
            queryWrapper.like(User::getUserAccount, userAccount);
            List<User> userList = userService.list(queryWrapper);
            List<User> users = userList.stream().map(userService::getSafeUser).collect(Collectors.toList());
            return ResultUtils.success(users);
        }else if(gender!=null){
            queryWrapper.like(User::getGender, gender);
            List<User> userList = userService.list(queryWrapper);
            List<User> users = userList.stream().map(userService::getSafeUser).collect(Collectors.toList());
            return ResultUtils.success(users);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> users = userList.stream().map(userService::getSafeUser).collect(Collectors.toList());
        return ResultUtils.success(users);
    }

    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUsersByTags(@RequestParam List<String> tagNameList, HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        List<User> users = userService.searchByTags(tagNameList,loginUser);
        return ResultUtils.success(users);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser=(User) userObj;
        if(currentUser==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userId=currentUser.getId();
        //todo 校验用户是否合法
        User user = userService.getById(userId);
        User safeUser = userService.getSafeUser(user);
        return ResultUtils.success(safeUser);
    }

    @PostMapping("/add")
    public BaseResponse<Integer> addUser(@RequestBody UserAddRequest addUser, HttpServletRequest request){
        if(!userService.isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH,"您没有该权限");
        }
        Integer result = userService.addUser(addUser);
        return ResultUtils.success(result);

    }

    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user,HttpServletRequest request){
        if(user==null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Integer result = userService.updateUser(user,loginUser);
        return ResultUtils.success(result);
    }

//    @PostMapping("/updateUser")
//    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest updateUser,HttpServletRequest request){
//        if(!isAdmin(request)){
//            throw new BusinessException(ErrorCode.NO_AUTH,"您没有该权限");
//        }
//        User user = new User();
//        BeanUtils.copyProperties(updateUser,user);
//        boolean result = userService.updateById(user);
//        if (result==false){
//            throw new BusinessException(ErrorCode.PARAM_ERROR,"更新失败");
//        }
//        return ResultUtils.success(result);
//    }

    /**
     * 用户自身修改
     * @param updateUser
     * @param request
     * @return
     */
    @PostMapping("/update/my")
    public BaseResponse<Boolean> updateUserByUser(@RequestBody UserUpdateRequest updateUser, HttpServletRequest request){
        User user = new User();
        BeanUtils.copyProperties(updateUser,user);
        boolean result = userService.updateById(user);
        if (result==false){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"更新失败");
        }
        return ResultUtils.success(result);
    }

    /**
     * 用户自身修改密码
     * @return
     */
    @PostMapping("/updatePassword")
    public BaseResponse<Boolean> modifyPassword(@RequestBody UserModifyPasswordRequest passwordRequest,HttpServletRequest request){
        if(userService.checkPassword(passwordRequest,request)){
            return ResultUtils.success(true);
        }else {
            return ResultUtils.error(ErrorCode.PARAM_ERROR);
        }
    }

    /**
     * 获取最匹配的用户
     */
    @GetMapping("/match")
    public BaseResponse<List<User>> matchUsers(Long num, HttpServletRequest request){
        if(num<=0||num>20){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userService.matchUsers(num,loginUser));
    }
}
