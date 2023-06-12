package com.usercenter;

import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Date 2023/5/31 10:36
 * author:wyf
 */
@SpringBootTest
public class RedissonTest {

    @Resource
    private RedissonClient redissonClient;

    @Test
    void test(){
        //list
        ArrayList<String> list = new ArrayList<>();
        list.add("awang");
        System.out.println(list.get(0));


        RList<String> rList = redissonClient.getList("test-list");
        rList.add("awang");
        System.out.println(rList.get(0));
        rList.remove(0);

        Map<String, Object> map = redissonClient.getMap("test-map");
        map.put("awang",10);
        map.put("atan",5);
    }
}
