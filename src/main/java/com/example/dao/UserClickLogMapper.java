package com.example.dao;

import com.example.entity.UserClickLog;

public interface UserClickLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserClickLog record);

    int insertSelective(UserClickLog record);

    UserClickLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserClickLog record);

    int updateByPrimaryKey(UserClickLog record);
}