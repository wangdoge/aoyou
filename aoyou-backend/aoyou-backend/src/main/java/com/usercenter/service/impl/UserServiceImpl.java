package com.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.usercenter.common.ErrorCode;
import com.usercenter.exception.BusinessException;
import com.usercenter.model.domain.User;
import com.usercenter.model.request.UserAddRequest;
import com.usercenter.model.request.UserModifyPasswordRequest;
import com.usercenter.model.vo.UserVO;
import com.usercenter.service.UserService;
import com.usercenter.mapper.UserMapper;
import com.usercenter.utils.AlgorithmUtils;
import kotlin.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.usercenter.contant.UserConstant.ADMIN_ROLE;
import static com.usercenter.contant.UserConstant.USER_LOGIN_STATE;

/**
* @author wang
* @description 针对表【user】的数据库操作Service实现
* @createDate 2023-05-08 22:24:15
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 加密密码
     */
    private static final String SALT="wang";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword,String username,String planetCode) {

        //1.校验
        //校验非空
        if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword,username)){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        //账号不小于4位
        if(userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"账号不可小于4位");
        }

        //密码不小于8位
        if(userPassword.length()<8||checkPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"密码不可小于8位");
        }

        //账户不能包含特殊字符
        String validPattern = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"两次输入密码不一致");
        }


        //账号不能重复
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount,userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if(count>0){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"账号重复");
        }

        //编号不能重复
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPlanetCode,planetCode);
        count = userMapper.selectCount(queryWrapper);
        if(count>0){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"床位已被注册");
        }

        //2.密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //3.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUsername(username);
        user.setPlanetCode(planetCode);
        boolean saveResult = this.save(user);
        if(!saveResult){
            return -1;
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1.校验
        //校验非空
        if(StringUtils.isAnyBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"参数为空");
        }

        //账号不小于4位
        if(userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"账号小于四位");
        }

        //密码不小于8位
        if(userPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"用户密码小于8位");
        }

        //账户不能包含特殊字符
        String validPattern = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"用户账号有特殊字符");
        }

        //2.密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT +   userPassword).getBytes());
        //查询用户是否存在
        LambdaQueryWrapper<User> userQueryWrapper = new LambdaQueryWrapper<>();
        userQueryWrapper.eq(User::getUserAccount,userAccount);
        User user = userMapper.selectOne(userQueryWrapper);
        //用户不存在
        if(user==null){
            log.info("user login failed,user account cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAM_ERROR,"用户不存在");
        }
        userQueryWrapper.eq(User::getUserPassword,encryptPassword);
        user = userMapper.selectOne(userQueryWrapper);
        //用户不存在
        if(user==null){
            log.info("user login failed,user account cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAM_ERROR,"用户密码有误");
        }


        //3.用户脱敏,隐藏返回给前端的敏感信息
        User safetyUser=getSafeUser(user);

        //4.记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);


        return safetyUser;
    }

    /**
     * 用户脱敏
     * @param user
     * @return
     */
    @Override
    public User getSafeUser(User user){
        if(user==null){
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUsername(user.getUsername());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setGender(user.getGender());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setUserStatus(user.getUserStatus());
        safetyUser.setCreateTime(user.getCreateTime());
        safetyUser.setUserRole(user.getUserRole());
        safetyUser.setPlanetCode(user.getPlanetCode());
        safetyUser.setTags(user.getTags());
        safetyUser.setProfile(user.getProfile());
        return safetyUser;
    }

    /**
     * 用户注销
     * @param request
     * @return
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    /**
     * 新增用户
     * @param addUser
     * @return
     */
    @Override
    public int addUser(UserAddRequest addUser) {
        User user = new User();
        BeanUtils.copyProperties(addUser,user);
        String userPassword=addUser.getUserPassword();
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        user.setUserPassword(encryptPassword);
        return userMapper.insert(user);
    }

    /**
     * 校验密码是否一致
     * @param passwordRequest
     * @param request
     * @return
     */
    @Override
    public boolean checkPassword(UserModifyPasswordRequest passwordRequest,HttpServletRequest request) {
        String password = passwordRequest.getPassword();
        //获取用户输入的密码的加密状态
        String userPassword = DigestUtils.md5DigestAsHex((SALT +password).getBytes());
        //获取当前用户
        User currentUser = getLoginUser(request);
        String checkPassword = userMapper.getUserPassword(currentUser.getId());

        if(userPassword.equals(checkPassword)){
            String newPassword = passwordRequest.getNewPassword();
            String newPassword1 = DigestUtils.md5DigestAsHex((SALT +newPassword).getBytes());
            currentUser.setUserPassword(newPassword1);
            userMapper.updateById(currentUser);
            return true;
        } else {
            throw new BusinessException(ErrorCode.PARAM_ERROR,"输入密码有误");
        }
    }

    /**
     * 获取当前登录的用户
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request==null){
            return null;
        }
        Object objUser = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user=(User) objUser;
        if(user==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN,"用户未登录");
        }
        return user;
    }

    /**
     * 根据标签搜索用户
     * @param tagList 用户拥有的标签
     * @return
     */
    @Override
    public List<User> searchByTags(List<String> tagList,User loginUser){
        if(CollectionUtils.isEmpty(tagList)){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"输入的标签为空");
        }
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        //查缓存
        String redisKey=String.format("aoyou:user:searchTags:%s",loginUser.getId());
        Page<User> userPage = (Page<User>)opsForValue.get(redisKey);
//        List<User> users = (List<User>)opsForValue.get(redisKey);
        if(userPage==null){
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            //todo 将page改为参数
            userPage = userMapper.selectPage(new Page<>(1,20),queryWrapper);
            opsForValue.set(redisKey,userPage);
        }
        List<User> users = userPage.getRecords();
        Gson gson = new Gson();
        //2在内存中查询
        return users.stream().filter(user -> {
            String tags = user.getTags();
            Set<String> fromJsonSet = gson.fromJson(tags, new TypeToken<Set<String >>() {}.getType());
            fromJsonSet = Optional.ofNullable(fromJsonSet).orElse(new HashSet<>());
            for (String tagName : tagList){
                if(!fromJsonSet.contains(tagName)){
                    return false;
                }
            }
            return true;
        }).map(this::getSafeUser).collect(Collectors.toList());
    }

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    @Override
    public Integer updateUser(User user, User loginUser) {
        Long userId = loginUser.getId();
        //如果是管理员，可以更新所有人
        //如果不是管理员，只能更新自己的
        if (!isAdmin(loginUser)&&userId!=loginUser.getId()){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User oldUser = userMapper.selectById(userId);
        if(oldUser==null){
            throw new BusinessException(ErrorCode.PARAM_NULL_ERROR);
        }
        return userMapper.updateById(user);
    }

    /**
     * 根据标签搜索用户(sql查询)
     * @param tagList
     * @return
     */
    @Deprecated
    private List<User> searchBySQL(List<String> tagList){
        if(CollectionUtils.isEmpty(tagList)){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"标签为空");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        for(String tagName: tagList){
            queryWrapper=queryWrapper.like("tags",tagName);
        }
        return userMapper.selectList(queryWrapper).stream().map(this::getSafeUser).collect(Collectors.toList());

    }

    /**
     * 是否为管理员
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request){
        Object userObject = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user=(User) userObject;
        return user!=null&&user.getUserRole()==ADMIN_ROLE;
    }

    /**
     * 是否为管理员
     * @param user
     * @return
     */
    @Override
    public boolean isAdmin(User user) {
        return user!=null&&user.getUserRole()==ADMIN_ROLE;
    }


    /**
     * 匹配用户
     * @param num
     * @param loginUser
     * @return
     */
    @Override
    public List<User> matchUsers(Long num, User loginUser) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.isNotNull(User::getTags);
        queryWrapper.select(User::getId,User::getTags);

        //获取所有用户
        List<User> userList = this.list(queryWrapper);
        //获取登录用户的tag
        String tags = loginUser.getTags();
        Gson gson = new Gson();
        //将登录用户的tag转为list集合
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        //map存用户列表下标，相似度
        List<Pair<User,Long>>list=new ArrayList<>();
        //遍历所有用户的tag
        for(int i=0;i<userList.size();i++){
            User user = userList.get(i);
            String userTags = user.getTags();
            //如果无标签
            if (StringUtils.isBlank(userTags) || user.getId().equals(loginUser.getId())){
                continue;
            }
            //获取到这个用户的tag
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            //计算分数
            System.out.println(tagList+" "+userTagList);
            long distance = AlgorithmUtils.minDistance(tagList, userTagList);
            list.add(new Pair<>(user,distance));
        }
        //按距离排序
        List<Pair<User, Long>> topUserPairList = list.stream()
                .sorted((a, b) -> (int) (a.getSecond() - b.getSecond()))
                .limit(num)
                .collect(Collectors.toList());

        //转化为有顺序的id列表
        List<Long> userListVO = topUserPairList.stream().map(pair -> pair.getFirst().getId()).collect(Collectors.toList());

        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.in(User::getId,userListVO);
        Map<Long, List<User>> userIdMap = this.list(userLambdaQueryWrapper).stream().
                map(user -> getSafeUser(user)).
                collect(Collectors.groupingBy(User::getId));

        List<User> finalUserList = new ArrayList<>();
        for (Long userId:userListVO){
            finalUserList.add(userIdMap.get(userId).get(0));
        }
        return finalUserList;
    }

}




