package com.usercenter;

import com.usercenter.model.domain.User;
import com.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Date 2023/5/29 21:08
 * author:wyf
 */
@SpringBootTest
@Slf4j
public class RedisTest {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private UserService userService;

    @Test
    void Test(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("nameString","wyf");
        valueOperations.set("nameInt",1);
        valueOperations.set("double",2.0);
        User user = new User();
        user.setUsername("wyf");
        user.setId(1L);
        valueOperations.set("user",user);

        //查
        Object nameString = valueOperations.get("nameString");
        Assertions.assertTrue("wyf".equals((String) nameString));
    }

    @Test
    void testWatchDog(){
        RLock lock = redissonClient.getLock("aoyou:precachejob:docache:lock");
        try {
            if(lock.tryLock(0,-1, TimeUnit.MILLISECONDS)){
                Thread.sleep(300000);
                System.out.println("getlock"+Thread.currentThread().getName());
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
