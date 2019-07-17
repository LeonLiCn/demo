package com.example.client.service.impl;

import com.example.client.dao.TestGoodsDao;
import com.example.client.dao.TestOrderDao;
import com.example.client.entity.TestGoodsModel;
import com.example.client.entity.TestOrderModel;
import com.example.client.service.GoodsService;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class GoodsServiceImpl implements GoodsService {

    private Lock lock = new ReentrantLock();

    private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("skill-test_thread").build();

    private ExecutorService pool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), 12, 0L,
            TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(1024 * 10), threadFactory, new ThreadPoolExecutor.AbortPolicy());
    @Resource
    private TestGoodsDao goodsDao;

    @Resource
    private TestOrderDao orderDao;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 使用jMeter压测3S 3K次请求
     * 1. database 添加data_version 数据处理异常，响应时间7S
     * 2. synchronized 锁定方法 耗时8S 数据处理成功
     * 3. lock锁定所有代码片段 耗时7S 数据处理成功
     * 4. 多线程处理 lock锁定所有代码段 耗时4S 数据处理成功
     * 5. redis原子自增处理 耗时5S 数据处理成功
     *
     */

    /**
     * synchronized
     * database 添加 data_version
     * @param id
     * @param username
     * @return
     */
    @Override
    public String buyGoods(Integer id, String username) {
        lock.lock();
        try {
            TestGoodsModel goodsModel = goodsDao.getById(id);
            if (goodsModel == null) {
                return "goods doesn`t exist !";
            }
            if (goodsModel.getCount() > 0) {
                int count = databaseExecute(goodsModel.getCount() - 1, id, username);
                if (count == 0) {
                    return "goods no have more";
                }
                return "success";
            }
            return "goods no have more";
        }finally {
            lock.unlock();
        }
    }

    @Override
    public String threadBuyGoods(Integer id, String username) {
        pool.execute(new BuyGoodsRunnable(id, username));
        return "success!";
    }

    @Override
    public Integer testRedis(String a) {
        stringRedisTemplate.opsForValue().set(a, 5 + "");
        return Integer.valueOf(stringRedisTemplate.opsForValue().get(a));
    }

    class BuyGoodsRunnable implements Runnable {

        private Integer id;
        private String username;

        public BuyGoodsRunnable(Integer id, String username) {
            this.id = id;
            this.username = username;
        }

        @Override
        public void run() {
            redisBuyGoods(id, username);
        }
    }

    public String redisBuyGoods(Integer id, String username) {
        String key = "product:" + id;
        Integer num = null;
        // 先检查 库存是否充足
        String numStr = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(numStr)) {
            TestGoodsModel goodsModel = goodsDao.getById(id);
            stringRedisTemplate.opsForValue().setIfAbsent(key, goodsModel.getCount() + "");
            num = goodsModel.getCount();
        } else {
            num = Integer.valueOf(numStr);
        }

        if (num <= 0) {
            return "goods no have more!";
        } else {
            //不可在这里下单减库存，否则导致数据不安全
        }

        //减少库存
        Long count = stringRedisTemplate.opsForValue().decrement(key);
        //库存充足
        if (count >= 0) {
            log.info("success");
            //TODO 真正减 扣 库存 等操作 下单等操作  ,这些操作可用通过 MQ 或 其他方式
            if (databaseExecute(Math.toIntExact(count), id, username) == 1) {
                return "success";
            }
        } else {
            //库存不足，需要增加刚刚减去的库存
            incr(key);
        }
        return "goods no have more!";
    }

    private Integer databaseExecute(Integer num, Integer id, String username) {
        TestGoodsModel updateGoodsModel = new TestGoodsModel();
        updateGoodsModel.setCount(num);
        updateGoodsModel.setId(id);

        TestOrderModel addOrderModel = new TestOrderModel();
        addOrderModel.setUsername(username);
        transactionTemplate.execute(state -> {
            try {
                int count = goodsDao.updateById(updateGoodsModel);
                if (count != 1) {
                    log.error("update goods fail !");
                    return 0;
                }
                count = orderDao.saveData(addOrderModel);
                if (count != 1) {
                    log.error("insert order fail !");
                    state.setRollbackOnly();
                    return 0;
                }
                return 1;
            } catch (Exception e) {
                e.printStackTrace();
                state.setRollbackOnly();
                return "system error ! ";
            }
        });
        return 1;
    }


    /**
     *
     * @param key
     * @return
     */
    private Long decr(String key) {
        RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, stringRedisTemplate.getConnectionFactory());
        Long count = entityIdCounter.getAndDecrement();
        RedisAtomicInteger redisAtomicInteger = new RedisAtomicInteger(key, stringRedisTemplate.getConnectionFactory());
        Integer a = redisAtomicInteger.getAndDecrement();
        return count;
    }

    /**
     *
     * @param key
     * @return
     */
    private Long incr(String key) {
        RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, stringRedisTemplate.getConnectionFactory());
        Long count = entityIdCounter.getAndIncrement();
        return count;
    }

}
