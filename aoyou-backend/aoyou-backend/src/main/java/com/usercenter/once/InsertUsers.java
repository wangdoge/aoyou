package com.usercenter.once;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import com.usercenter.mapper.UserMapper;
import com.usercenter.model.domain.User;
import com.usercenter.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

/**
 * Date 2023/5/29 13:37
 * author:wyf
 */

@Component
public class InsertUsers {

    @Resource
    private UserMapper userMapper;
    @Resource
    private UserService userService;

    private ExecutorService executorService= new ThreadPoolExecutor(60,1000,10000,TimeUnit.MINUTES,new ArrayBlockingQueue<>(10000));

//    @Scheduled(initialDelay = 5000,fixedRate = Long.MAX_VALUE)
    private void insertUsers(){
        StopWatch stopWatch=new StopWatch();
        stopWatch.start();
        final int INSERT_NUM=2500;
        int j=0;
        List<CompletableFuture<Void>> futures=new ArrayList<>();
        for(int i=0;i<200;i++){
            ArrayList<User> userList = new ArrayList<>();
            while (true){
                j++;
                User user = new User();
                user.setUsername("假用户");
                user.setAvatarUrl("fake");
                user.setUserAccount("https://ts1.cn.mm.bing.net/th/id/R-C.73604729b5e476440e6bf96ce116561d?rik=lwm2bom16o93dg&riu=http%3a%2f%2fseopic.699pic.com%2fphoto%2f50074%2f0922.jpg_wh1200.jpg&ehk=HAVlCue%2f0poUh301DlyPoSo3FF89prq31X5b2%2boEQ%2f0%3d&risl=&pid=ImgRaw&r=0");
                user.setGender(0);
                user.setUserPassword("12345678");
                user.setPhone("123");
                user.setEmail("123@qq.com");
                user.setUserStatus(0);
                user.setPlanetCode("1111");
                user.setUserRole(0);
                user.setTags("");
                user.setProfile("");
                userList.add(user);
                if(j%5000==0){
                    break;
                }
            }
            CompletableFuture<Void>future= CompletableFuture.runAsync(()->{
                System.out.println("threadname"+Thread.currentThread().getName());
                userService.saveBatch(userList,10000);
            },executorService);
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{})).join();
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

}
