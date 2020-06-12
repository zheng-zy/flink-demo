package com.example.entity;

import lombok.Data;

import java.util.Date;

@Data
public class UserClick {

    private Integer id;
    private Integer userId;
    private Date clickTime;

}
