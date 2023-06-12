package com.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.usercenter.common.ErrorCode;
import com.usercenter.exception.BusinessException;
import com.usercenter.mapper.TeamMapper;
import com.usercenter.model.domain.Team;
import com.usercenter.model.domain.TeamName;
import com.usercenter.model.domain.User;
import com.usercenter.model.dto.TeamQuery;
import com.usercenter.model.enums.TeamStatusEnum;
import com.usercenter.model.request.TeamDeleteRequest;
import com.usercenter.model.request.TeamJoinRequest;
import com.usercenter.model.request.TeamQuitRequest;
import com.usercenter.model.request.TeamUpdateRequest;
import com.usercenter.model.vo.TeamNameVO;
import com.usercenter.model.vo.UserVO;
import com.usercenter.service.TeamNameService;
import com.usercenter.service.TeamService;
import com.usercenter.service.UserService;
import io.swagger.models.auth.In;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
* @author wang
* @description 针对表【team】的数据库操作Service实现
* @createDate 2023-05-31 19:58:24
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private TeamNameService teamNameService;

    @Resource
    private UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        final Long userId = loginUser.getId();

        //请求参数为空?
        if(team==null){
             throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        //是否登录
        if(loginUser==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        //队伍人数大于1小于20
        Integer maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if(maxNum<1||maxNum>20){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"队伍人数不满足要求");
        }
        //队伍标题<=20
        String title = team.getName();
        if(StringUtils.isBlank(title)||title.length()>20){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"队伍标题不满足要求");
        }
        //描述<512
        String description = team.getDescription();
        if(description.length()>512){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"队伍描述过长");
        }
        //status是否公开
        Integer status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumById(status);
        if(statusEnum==null){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"队伍状态异常");
        }
        //队伍是否加密
        String password = team.getPassword();
        if(TeamStatusEnum.SCRECT.equals(statusEnum)){
            if (StringUtils.isBlank(password)||password.length()>32){
                throw new BusinessException(ErrorCode.PARAM_ERROR,"密码格式有误");
            }
        }
        //超时时间>当前时间
        Date expireTime = team.getExpireTime();
        if(new Date().after(expireTime)){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"队伍超时");
        }
        //用户最多创五个队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        long teamNum = this.count(queryWrapper);
        if(teamNum>=5){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"用户最多创建五哥队伍");
        }
        //插入信息到队伍表
        team.setId(null);
        team.setUserId(userId);
        boolean result = this.save(team);
        Long teamId = team.getId();

        if(!result){
             throw new BusinessException(ErrorCode.PARAM_ERROR,"创建队伍失败");
        }
        //插入用户队伍信息关系表
        TeamName teamName = new TeamName();
        teamName.setTeamId(teamId);
        teamName.setUserId(userId);
        teamName.setJoinTime(new Date());
        result=teamNameService.save(teamName);
        if(!result){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"创建队伍失败");
        }
        return teamId;
    }

    @Override
    public List<TeamNameVO> listTeams(TeamQuery teamQuery, boolean isAdmin) {
        LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();
        if(teamQuery!=null){
            Long id = teamQuery.getId();
            if(id!=null&&id>0){
                queryWrapper.eq(Team::getId,id);
            }
            //使用队伍列表查询
            List<Long> idList = teamQuery.getIdList();
            if(!CollectionUtils.isEmpty(idList)){
                queryWrapper.in(Team::getId,idList);
            }
            String searchText = teamQuery.getSearchText();
            if(StringUtils.isNotBlank(searchText)){
                queryWrapper.and(qw->qw.like(Team::getName,searchText).or().like(Team::getDescription,searchText));
            }
            String name = teamQuery.getName();
            if(StringUtils.isNotBlank(name)){
                queryWrapper.like(Team::getName,name);
            }
            String description = teamQuery.getDescription();
            if(StringUtils.isNotBlank(description)){
                queryWrapper.like(Team::getDescription,description);
            }
            Integer maxNum = teamQuery.getMaxNum();
            if(maxNum != null && maxNum > 0){
                queryWrapper.eq(Team::getMaxNum,maxNum);
            }
            Long userId = teamQuery.getUserId();
            if(userId!=null&&userId>0){
                queryWrapper.eq(Team::getUserId,userId);
            }

            Integer status = teamQuery.getStatus();
            TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumById(status);
            List<Integer> teamStatusEnumList = new ArrayList<>();
            if (teamStatusEnum==null){
                teamStatusEnumList.add(TeamStatusEnum.PUBLIC.getValue());
                teamStatusEnumList.add(TeamStatusEnum.SCRECT.getValue());
            }

            if(!isAdmin&&(Objects.equals(teamStatusEnum,TeamStatusEnum.PRIVATE))){
                throw new BusinessException(ErrorCode.NO_AUTH,"无管理员权限");
            }
            queryWrapper.in(Team::getStatus,teamStatusEnumList);
        }
        //不展示已过期的队伍
        queryWrapper.and(qw->qw.gt(Team::getExpireTime,new Date()).or().isNull(Team::getExpireTime));

        List<Team> teamList = this.list(queryWrapper);
        if(CollectionUtils.isEmpty(teamList)) {
            return new ArrayList<>();
        }

        List<TeamNameVO> teamUserVOList=new ArrayList<>();
        //关联查询创建人的用户信息
        for(Team team:teamList){
            Long userId = team.getUserId();
            if(userId==null){
                continue;
            }
            User user = userService.getById(userId);
            TeamNameVO teamNameVO = new TeamNameVO();
            BeanUtils.copyProperties(team,teamNameVO);
            //脱敏用户信息
            if(user!=null){
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user,userVO);
                teamNameVO.setCreateUser(userVO);
            }
            teamUserVOList.add(teamNameVO);
        }
        return teamUserVOList;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest,User loginUser) {
        if(teamUpdateRequest==null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        Long id = teamUpdateRequest.getId();
        if(id==null||id<=0){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        //获取原先的队伍信息
        Team oldTeam = getTeam(id);
        //管理员和队长可更改队伍信息
        if(oldTeam.getUserId()!=loginUser.getId()&&!userService.isAdmin(loginUser)){
            throw new BusinessException(ErrorCode.NO_AUTH,"您无权修改");
        }
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumById(teamUpdateRequest.getStatus());
        if(statusEnum.equals(TeamStatusEnum.SCRECT)){
            if (StringUtils.isBlank(teamUpdateRequest.getPassword())){
                throw new BusinessException(ErrorCode.PARAM_ERROR,"加密房间需要有密码");
            }
        }
        if(statusEnum.equals(TeamStatusEnum.PUBLIC)){
            if (StringUtils.isNotBlank(teamUpdateRequest.getPassword())){
                throw new BusinessException(ErrorCode.PARAM_ERROR,"公开房间不需要有密码");
            }
        }
        //todo 如果传入的值和原来的值一致，则不修改
        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest,updateTeam);
        if(!updateTeam.equals(oldTeam)){
            return this.updateById(updateTeam);
        }
       return true;
    }

    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        if(teamJoinRequest==null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        Long userId = loginUser.getId();
        RLock lock = redissonClient.getLock("aoyou:join_team:lock");
        LambdaQueryWrapper<TeamName> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeamName::getUserId,userId);
        try {
            while(true){
                if(lock.tryLock(0,-1, TimeUnit.MILLISECONDS)){
                    //查询已加入的队伍人数
                    long hasJoinNum = teamNameService.count(queryWrapper);
                    if(hasJoinNum>5){
                        throw new BusinessException(ErrorCode.PARAM_ERROR,"最多加入五个队伍");
                    }
                    Long teamId = teamJoinRequest.getTeamId();
                    if (teamId==null||teamId<=0){
                        throw new BusinessException(ErrorCode.PARAM_ERROR);
                    }
                    //查询该房间
                    Team team = getTeam(teamId);
                    //不可重复加入已加入的队伍
                    queryWrapper=new LambdaQueryWrapper<>();
                    queryWrapper.eq(TeamName::getTeamId,teamId);
                    queryWrapper.eq(TeamName::getUserId,userId);
                    long hasUserJoinTeam = teamNameService.count(queryWrapper);
                    if (hasUserJoinTeam>0){
                        throw new BusinessException(ErrorCode.PARAM_ERROR,"您已经在队伍中辣");
                    }
                    //校验过期时间
                    if(team!=null&&team.getExpireTime().before(new Date())){
                        throw new BusinessException(ErrorCode.PARAM_ERROR,"队伍已过期");
                    }
                    //校验队伍私有性
                    Integer status = team.getStatus();
                    TeamStatusEnum statusEnum = TeamStatusEnum.getEnumById(status);
                    if(TeamStatusEnum.PRIVATE.equals(statusEnum)){
                        throw new BusinessException(ErrorCode.NO_AUTH  ,"不可加入私有队伍");
                    }
                    //校验房间密码
                    String password = teamJoinRequest.getPassword();
                    if(TeamStatusEnum.SCRECT.equals(statusEnum)){
                        if (!password.equals(team.getPassword())){
                            throw new BusinessException(ErrorCode.PARAM_ERROR,"房间密码错误");
                        }
                    }

                    //已加入队伍的人数
                    long teamHasJoinNums = countTeamUserById(teamId);
                    if(teamHasJoinNums>=team.getMaxNum()){
                        throw new BusinessException(ErrorCode.PARAM_ERROR,"队伍已满");
                    }
                    TeamName teamName = new TeamName();
                    teamName.setUserId(userId);
                    teamName.setTeamId(teamId);
                    teamName.setJoinTime(new Date());
                    return teamNameService.save(teamName);
                }
            }
        } catch (InterruptedException e) {
            log.error("redis error:",e);
            return false;
        }finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()){
                System.out.println("unlock"+Thread.currentThread().getName());
                lock.unlock();
            }
        }

    }

    @Override
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        Long teamId = teamQuitRequest.getTeamId();
        Team team = getTeam(teamId);
        Long userId = loginUser.getId();
        //找到该条关系
        LambdaQueryWrapper<TeamName> userQueryWrapper = new LambdaQueryWrapper<>();
        userQueryWrapper.eq(TeamName::getTeamId,teamId);
        userQueryWrapper.eq(TeamName::getUserId,userId);
        long count = teamNameService.count(userQueryWrapper);
        if (count==0){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"您未加入队伍");
        }
        //查询该队伍剩余人数
        long teamHasJoinNum = countTeamUserById(teamId);
        //队伍只剩1人,队伍解散
        if(teamHasJoinNum==1){
            //删除该队伍
            this.removeById(teamId);
        }else{
            //该用户为队长
            if(team.getUserId().equals(userId)){
                //把队长转移给最早加入的人
                //查询加入队伍的所有用户
                LambdaQueryWrapper<TeamName> teamNameQueryWrapper = new LambdaQueryWrapper<>();
                teamNameQueryWrapper.eq(TeamName::getTeamId,teamId);
                teamNameQueryWrapper.last("order by id asc limit 2");
                List<TeamName> teamNameList = teamNameService.list(teamNameQueryWrapper);
                if(CollectionUtils.isEmpty(teamNameList)||teamNameList.size()!=2){
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                //将队长设置为这个人
                TeamName teamName = teamNameList.get(1);
                Long nextTeamLeader = teamName.getUserId();
                Team updateTeam = new Team();
                updateTeam.setId(teamId);
                updateTeam.setUserId(nextTeamLeader);
                //更新队长
                boolean updateResult = this.updateById(updateTeam);
                if (!updateResult){
                    throw new BusinessException(ErrorCode.PARAM_ERROR,"更新队长失败");
                }
            }
            //移除队伍-用户关系
            return teamNameService.remove(userQueryWrapper);
        }
        return teamNameService.remove(userQueryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(TeamDeleteRequest teamDeleteRequest, User loginUser) {
        Long teamId = teamDeleteRequest.getTeamId();
        Team team = getTeam(teamId);
        //是否为队长
        if (!team.getUserId().equals(loginUser.getId())){
            throw new BusinessException(ErrorCode.NO_AUTH,"您不是队长");
        }
        //删除关联信息和该队伍
        LambdaQueryWrapper<TeamName> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeamName::getTeamId,teamId);
        boolean result = teamNameService.remove(queryWrapper);
        if(!result){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        return this.removeById(teamId);
    }

    /**
     * 获取某队伍当前人数
     * @param teamId
     * @return
     */
    private long countTeamUserById(long teamId){
        LambdaQueryWrapper<TeamName> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeamName::getTeamId,teamId);
        //查询该用户是否存在队伍中
        long count = teamNameService.count(queryWrapper);
        return count;
    }

    /**
     * 根据id获取队伍信息
     * @param teamId
     * @return
     */
    private Team getTeam(Long teamId) {
        if(teamId ==null|| teamId <0){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        Team team = this.getById(teamId);
        if(team==null){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"队伍不存在");
        }
        return team;
    }

}




