package com.usercenter.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usercenter.model.domain.User;
import com.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Date 2023/5/29 22:52
 * author:wyf
 */
@Slf4j
@Component
public class PreCacheJob {

    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate<String,Object>redisTemplate;
    @Resource
    private RedissonClient redissonClient;
    //重点用户
    List<Long> mainUser= Arrays.asList(1L);

    @Scheduled(cron = "0 46 11 * * *")
    public void doCaCheRecommend(){
        RLock lock = redissonClient.getLock("aoyou:precachejob:docache:lock");
        try {
            if(lock.tryLock(0,-1,TimeUnit.MILLISECONDS)){
                for (Long userId:mainUser){
                    System.out.println("getlock"+Thread.currentThread().getName());
                    String redisKey=String.format("aoyou:user:recommend:%s",userId);
                    ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
                    LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1,20),queryWrapper);
                    try {
                        opsForValue.set(redisKey,userPage,1, TimeUnit.HOURS);
                    }catch (Exception e){
                        log.error("redis error:",e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("redis error:",e);
        } finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()){
                System.out.println("unlock"+Thread.currentThread().getName());
                lock.unlock();
            }
        }
    }
}
