package com.example.service.impl;

import com.example.dao.UserClickLogMapper;
import com.example.entity.UserClickLog;
import com.example.service.UserClickLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * <p></p>
 * Created by @author zhezhiyong@163.com on 2020/6/11.
 */
@Service
@Slf4j
public class UserClickLogServiceImpl implements UserClickLogService {

    @Autowired
    private UserClickLogMapper userClickLogMapper;

    @Override
    @Cacheable(value = "getUserClickLog", key = "methodName.concat(':').concat(#p0.toString())")
    public UserClickLog getUserClickLog(String key) {
        log.info("test key:{}", key);
        int id = Integer.parseInt(key);
        UserClickLog userClickLog = userClickLogMapper.selectByPrimaryKey(id);
        return userClickLog;
    }
}
