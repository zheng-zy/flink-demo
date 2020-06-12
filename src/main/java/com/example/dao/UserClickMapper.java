package com.example.dao;


import com.example.entity.UserClick;

import java.util.List;

public interface UserClickMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(UserClick record);

    int insertSelective(UserClick record);

    UserClick selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserClick record);

    int updateByPrimaryKey(UserClick record);

    List<UserClick> findAll();

}
