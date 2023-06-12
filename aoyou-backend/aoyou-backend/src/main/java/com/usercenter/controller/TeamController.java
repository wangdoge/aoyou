package com.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usercenter.common.BaseResponse;
import com.usercenter.common.ErrorCode;
import com.usercenter.common.ResultUtils;
import com.usercenter.exception.BusinessException;
import com.usercenter.model.domain.Team;
import com.usercenter.model.domain.TeamName;
import com.usercenter.model.domain.User;
import com.usercenter.model.dto.TeamQuery;
import com.usercenter.model.request.*;
import com.usercenter.model.vo.TeamNameVO;
import com.usercenter.model.vo.UserVO;
import com.usercenter.service.TeamNameService;
import com.usercenter.service.TeamService;
import com.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Date 2023/5/10 19:01
 * author:wyf
 */
@RestController
@RequestMapping("/team")
@CrossOrigin()
@Slf4j
public class TeamController {

    @Resource
    private TeamService teamService;

    @Resource
    private UserService userService;

    @Resource
    private TeamNameService teamNameService;

    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest,HttpServletRequest request){
        if(teamAddRequest==null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest,team);
        User loginUser = userService.getLoginUser(request);
        long result = teamService.addTeam(team, loginUser);
        return ResultUtils.success(result);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody TeamDeleteRequest teamDeleteRequest,HttpServletRequest request){
        if(teamDeleteRequest==null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.deleteTeam(teamDeleteRequest,loginUser);
        if(!result){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"删除失败");
        }
        return ResultUtils.success(true);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest,HttpServletRequest request){
        if(teamUpdateRequest==null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.updateTeam(teamUpdateRequest,loginUser);
        if(!result){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"更新失败");
        }
        return ResultUtils.success(true);
    }

    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(long id){
        if(id<=0){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        Team team = teamService.getById(id);
        if(team==null){
            throw new BusinessException(ErrorCode.PARAM_NULL_ERROR);
        }
        return ResultUtils.success(team);
    }

    @GetMapping("/list")
    public BaseResponse<List<TeamNameVO>> listTeam(TeamQuery teamQuery,HttpServletRequest request){
        if (teamQuery==null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean isAdmin = userService.isAdmin(loginUser);
        List<TeamNameVO> teamList = teamService.listTeams(teamQuery,isAdmin);

        final List<Long> teamIdList=teamList.stream().map(TeamNameVO::getId).collect(Collectors.toList());
        //判断是否加入了队伍
        LambdaQueryWrapper<TeamName> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeamName::getUserId,loginUser.getId());
        queryWrapper.in(TeamName::getTeamId,teamIdList);
        //查询当前登录用户在哪些队伍中
        List<TeamName> userTeamList = teamNameService.list(queryWrapper);
        //生成该用户已登录的队伍的id的集合
        Set<Long> hasJoinTeamSet = userTeamList.stream().map(TeamName::getTeamId).collect(Collectors.toSet());
        System.out.println(hasJoinTeamSet);
        teamList.forEach(team->{
            //如果加入队伍列表中有该队伍，将其设置为已加入
            boolean hasJoin = hasJoinTeamSet.contains(team.getId());
            team.setHasJoin(hasJoin);
        });

        //查询加入队伍的用户信息(人数)
        LambdaQueryWrapper<TeamName> userTeamJoinQueryWrapper = new LambdaQueryWrapper<>();
        userTeamJoinQueryWrapper.in(TeamName::getTeamId,teamIdList);
        List<TeamName> teamUserList = teamNameService.list(userTeamJoinQueryWrapper);
        Map<Long, List<TeamName>> teamIdUserList = teamUserList.stream().collect(Collectors.groupingBy(TeamName::getTeamId));


        teamList.forEach(team->{
            team.setHasJoinNum(teamIdUserList.get(team.getId()).size());
            List<TeamName> nameList = teamIdUserList.get(team.getId());
            //找出每个队的队员
            List<UserVO> userVOList = new ArrayList<>();
            nameList.forEach(teamName->{
                UserVO userVO = new UserVO();
                User userById = userService.getById(teamName.getUserId());
                BeanUtils.copyProperties(userById,userVO);
                userVOList.add(userVO);
            });
            System.out.println(userVOList);
            team.setUserList(userVOList);
        });

        //查询在队伍中的成员

        return ResultUtils.success(teamList);

    }

    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamByPage(TeamQuery teamQuery){
        if (teamQuery==null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        Team team = new Team();
        Page<Team> teamPage = new Page<>(teamQuery.getPageNum(),teamQuery.getPageSize());
        BeanUtils.copyProperties(teamQuery,team);
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> resultPage = teamService.page(teamPage,queryWrapper);
        return ResultUtils.success(resultPage);

    }

    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request){
        if(teamJoinRequest==null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result= teamService.joinTeam(teamJoinRequest,loginUser);
        return ResultUtils.success(result);
    }

    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request){
        if(teamQuitRequest==null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result= teamService.quitTeam(teamQuitRequest,loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 获取我创建的队伍
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/my/create")
    public BaseResponse<List<TeamNameVO>> listMyCreateTeam(TeamQuery teamQuery,HttpServletRequest request){
        if (teamQuery==null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        teamQuery.setUserId(loginUser.getId());
        List<TeamNameVO> list = teamService.listTeams(teamQuery,true);
        return ResultUtils.success(list);

    }

    /**
     * 获取我加入的队伍
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamNameVO>> listMyJoinTeam(TeamQuery teamQuery,HttpServletRequest request){
        if (teamQuery==null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        User loginUser = userService.getLoginUser(request);

        //获取当前用户加入的队伍
        QueryWrapper<TeamName> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",loginUser.getId());
        List<TeamName> userTeamList = teamNameService.list(queryWrapper);
        //id去重
        // teamid  userid
        //  1        2
        //  1        3
        //  2        3
        // 1=>2,3  2=>3

        Map<Long, List<TeamName>> listMap = userTeamList.stream()
                .collect(Collectors.groupingBy((TeamName::getTeamId)));
        ArrayList<Long> idList = new ArrayList<>(listMap.keySet());
        teamQuery.setIdList(idList);

        List<TeamNameVO> list = teamService.listTeams(teamQuery,true);
        return ResultUtils.success(list);

    }
}
