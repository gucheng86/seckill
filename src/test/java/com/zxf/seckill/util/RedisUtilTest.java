package com.zxf.seckill.util;

import com.zxf.seckill.dto.SeckillExecution;
import com.zxf.seckill.dto.SeckillResult;
import com.zxf.seckill.enums.SeckillStateEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = "classpath:spring/spring-redis.xml")
public class RedisUtilTest {
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Test
    public void test() {
        System.out.println(redisUtil.getRedisTemplate().getValueSerializer());
        System.out.println(redisUtil.getRedisTemplate().getKeySerializer());

        redisUtil.putResult(1, 1, SeckillResult.success(SeckillExecution.unsuccess(1, SeckillStateEnum.TIME_END)));
        System.out.println(redisUtil.getResult(1, 1));
    }


    @Test
    public void test3() throws InterruptedException {
        String key = "id";
        boolean result;
//        redisUtil.put("id", 10);
//        redisUtil.decr("id");
//        System.out.println(redisUtil.get("id"));
//        redisUtil.decr("id");
//        System.out.println(redisUtil.get("id"));

//        redisUtil.deleteLock(key);
        result = redisUtil.getLock(key);
        System.out.println(result);
//        redisUtil.deleteLock(key);
        result = redisUtil.getLock(key);
        System.out.println(result);
//        redisUtil.deleteLock(key);

    }

    @Test
    public void test4() {
        String key = "id";
        redisUtil.put(key, 10);
//        redisUtil.getRedisTemplate().opsForValue().increment(key, -1);
        redisUtil.deleteLock(key);

        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread() {
                public void run() {
                    redisUtil.decr("id");
//                    redisUtil.getRedisTemplate().opsForValue().increment(key, -1);

                }
            };
            thread.start();

        }

        System.out.println(redisUtil.get("id"));


    }

    @Test
    public void test5() {
        redisUtil.put("id", 1);
        Long id = redisUtil.getRedisTemplate().opsForValue().increment("id", -1);
        System.out.println(id);
        Long id2 = redisUtil.getRedisTemplate().opsForValue().increment("id", -1);
        System.out.println(id2);

    }
}