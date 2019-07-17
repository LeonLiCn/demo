package com.example.client.entity;

import lombok.Data;

import java.util.Date;

@Data
public class TestOrderModel {

    private Integer id;
    private String username;
    private Integer orderState;
    private Date addTime;

}
