package com.example.function;

import com.alibaba.fastjson.JSON;
import com.example.config.NacosConfig;
import com.example.dao.UserClickLogMapper;
import com.example.entity.UserClickLog;
import com.example.service.UserClickLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;
import java.util.Random;

import static com.example.config.Constant.DATA_ID;
import static com.example.config.Constant.GROUP;

/**
 * <p></p>
 * Created by @author zhezhiyong@163.com on 2020/6/5.
 */
@Slf4j
public class InsertMapFunction extends RichMapFunction<String, UserClickLog> {

    private UserClickLogMapper userClickLogMapper;
    private ApplicationContext applicationContext;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        applicationContext = new ClassPathXmlApplicationContext("classpath*:application.xml");
        userClickLogMapper = (UserClickLogMapper) applicationContext.getBean("userClickLogMapper");
        NacosConfig.initConfigIns(GROUP, DATA_ID);
    }

    @Override
    public UserClickLog map(String s) throws Exception {
        Random random = new Random();
        UserClickLog userClickLog = new UserClickLog();
        int uid = random.nextInt(Integer.MAX_VALUE);
        userClickLog.setUserId(uid);
        userClickLog.setMsg(s);
        userClickLog.setClickTime(new Date());
        if (NacosConfig.properties.getOrDefault("isInsert", "true").equals("false")) {
            log.info("不插入db数据:{}", s);
        } else {
            userClickLogMapper.insertSelective(userClickLog);
            log.info("插入db数据:{}", JSON.toJSONString(userClickLog));
        }

        UserClickLogService userClickLogService = (UserClickLogService) applicationContext.getBean("userClickLogServiceImpl");
        userClickLog = userClickLogService.getUserClickLog("5");
        System.out.println("userClickLog1 = " + JSON.toJSONString(userClickLog));
        userClickLog = userClickLogService.getUserClickLog("6");
        System.out.println("userClickLog2 = " + JSON.toJSONString(userClickLog));
        userClickLog = userClickLogService.getUserClickLog("5");
        System.out.println("userClickLog3 = " + JSON.toJSONString(userClickLog));

        return userClickLog;
    }
}
