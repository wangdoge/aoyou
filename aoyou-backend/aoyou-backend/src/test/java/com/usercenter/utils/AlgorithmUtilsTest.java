package com.usercenter.utils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Date 2023/6/8 10:33
 * author:wyf
 */
@SpringBootTest
class AlgorithmUtilsTest {

    @Test
    void Test(){
        List<String> strings1 = Arrays.asList("英雄联盟","艾欧尼亚","王者","男");
        List<String> strings2 = Arrays.asList("英雄联盟","暗影岛","王者","男");
        List<String> strings3 = Arrays.asList("英雄联盟","暗影岛","铂金","男");
        System.out.println(strings1);
        System.out.println(AlgorithmUtils.minDistance(strings1, strings2));
        System.out.println(AlgorithmUtils.minDistance(strings1, strings3));
        System.out.println(AlgorithmUtils.minDistance(strings2, strings3));
    }
}