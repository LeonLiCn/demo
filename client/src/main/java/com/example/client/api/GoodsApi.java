package com.example.client.api;

import com.example.client.dao.TestGoodsDao;
import com.example.client.entity.TestGoodsModel;
import com.example.client.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class GoodsApi {

    @Autowired
    private GoodsService goodsService;



    @GetMapping(value = "/test" )
    public Object test(String a) {
/*        stringRedisTemplate.opsForValue().setIfAbsent(a, 3 + "");
        Long b = stringRedisTemplate.opsForValue().increment(a);
        return stringRedisTemplate.opsForValue().get(a);*/
        return null;
    }

    @GetMapping(value = "/goods/buy")
    public String buy(@RequestParam("id") Integer id, @RequestParam("username") String name) {
        //return goodsService.buyGoods(id, name);
        return goodsService.threadBuyGoods(id, name);
    }

}
