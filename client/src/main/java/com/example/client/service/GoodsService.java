package com.example.client.service;

public interface GoodsService {

    String buyGoods(Integer id, String username);

    String threadBuyGoods(Integer id, String username);

    Integer testRedis(String a);

}
