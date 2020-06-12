package com.example.function;

import com.alibaba.fastjson.JSON;
import com.example.dao.UserClickLogMapper;
import com.example.dao.UserClickMapper;
import com.example.entity.UserClick;
import com.example.entity.UserClickLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * <p></p>
 * Created by @author zhezhiyong@163.com on 2020/6/5.
 */
@Slf4j
public class RedisSinkFunction extends RichSinkFunction<UserClickLog> {

    private UserClickLogMapper userClickLogMapper;
    private UserClickMapper userClickMapper;
    private ApplicationContext applicationContext;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        applicationContext = new ClassPathXmlApplicationContext("classpath*:application.xml");
        userClickLogMapper = (UserClickLogMapper) applicationContext.getBean("userClickLogMapper");
        userClickMapper = (UserClickMapper) applicationContext.getBean("userClickMapper");
    }

    @Override
    public void invoke(UserClickLog value, Context context) throws Exception {
        log.info("插入redis数据:{}", JSON.toJSONString(value));
        List<UserClick> userClickList = userClickMapper.findAll();
        userClickList.forEach(o -> log.info(o.toString()));
    }
}
