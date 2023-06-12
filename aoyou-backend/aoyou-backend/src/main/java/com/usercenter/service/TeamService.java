package com.usercenter.service;

import com.usercenter.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.usercenter.model.domain.User;
import com.usercenter.model.dto.TeamQuery;
import com.usercenter.model.request.TeamDeleteRequest;
import com.usercenter.model.request.TeamJoinRequest;
import com.usercenter.model.request.TeamQuitRequest;
import com.usercenter.model.request.TeamUpdateRequest;
import com.usercenter.model.vo.TeamNameVO;

import java.util.List;

/**
* @author wang
* @description 针对表【team】的数据库操作Service
* @createDate 2023-05-31 19:58:24
*/
public interface TeamService extends IService<Team> {

    /**
     * 添加用户
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);


    /**
     * 搜素队伍
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    List<TeamNameVO> listTeams(TeamQuery teamQuery,boolean isAdmin);

    /**
     * 更新队伍
     * @param teamUpdateRequest
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest,User loginUser);

    /**
     * 加入队伍
     * @param teamJoinRequest
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    /**
     * 退出队伍
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    /**
     * 队长解散队伍
     * @param teamDeleteRequest
     * @param loginUser
     * @return
     */
    boolean deleteTeam(TeamDeleteRequest teamDeleteRequest,User loginUser);
}
