package com.example;

import com.alibaba.fastjson.JSON;
import com.example.config.NacosConfig;
import com.example.dao.UserClickLogMapper;
import com.example.entity.UserClickLog;
import com.example.service.UserClickLogService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static com.example.config.Constant.DATA_ID;
import static com.example.config.Constant.GROUP;


/**
 * Unit test for simple App.
 */
@Slf4j
public class AppTest {
    private UserClickLogMapper userClickLogMapper;
    private ApplicationContext applicationContext;

    @Test
    public void test12() throws InterruptedException {
        NacosConfig.initConfigIns(GROUP, DATA_ID);
        applicationContext = new ClassPathXmlApplicationContext("classpath*:application.xml");
        userClickLogMapper = (UserClickLogMapper) applicationContext.getBean("userClickLogMapper");
        UserClickLogService userClickLogService = (UserClickLogService) applicationContext.getBean("userClickLogServiceImpl");
        UserClickLog userClickLog = userClickLogService.getUserClickLog("5");
        System.out.println("userClickLog1 = " + JSON.toJSONString(userClickLog));
        userClickLog = userClickLogService.getUserClickLog("6");
        System.out.println("userClickLog2 = " + JSON.toJSONString(userClickLog));
        userClickLog = userClickLogService.getUserClickLog("5");
        System.out.println("userClickLog3 = " + JSON.toJSONString(userClickLog));
    }


}
