package com.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.usercenter.model.domain.TeamName;
import com.usercenter.service.TeamNameService;
import com.usercenter.mapper.TeamNameMapper;
import org.springframework.stereotype.Service;

/**
* @author wang
* @description 针对表【team_name】的数据库操作Service实现
* @createDate 2023-05-31 20:05:01
*/
@Service
public class TeamNameServiceImpl extends ServiceImpl<TeamNameMapper, TeamName>
    implements TeamNameService{

}




