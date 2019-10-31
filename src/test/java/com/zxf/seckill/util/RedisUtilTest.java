package com.zxf.seckill.util;

import com.zxf.seckill.dto.SeckillExecution;
import com.zxf.seckill.dto.SeckillResult;
import com.zxf.seckill.entity.Seckill;
import com.zxf.seckill.enums.SeckillStateEnum;
import com.zxf.seckill.service.SeckillService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = {"classpath:spring/spring-redis.xml",
                                "classpath:spring/spring-service.xml"})
public class RedisUtilTest {
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    SeckillService seckillService;

    @Test
    public void testSerializer() {
        System.out.println(redisUtil.getRedisTemplate().getValueSerializer());
        System.out.println(redisUtil.getRedisTemplate().getKeySerializer());

        redisUtil.putResult(1, 1, SeckillResult.success(SeckillExecution.unsuccess(1, SeckillStateEnum.TIME_END)));
        System.out.println(redisUtil.getResult(1, 1));
    }


    @Test
    public void testLock() throws InterruptedException {
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
    public void testDecr() {
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
    public void testIncrement() {
        redisUtil.put("id", 1);
        Long id = redisUtil.getRedisTemplate().opsForValue().increment("id", -1);
        System.out.println(id);
        Long id2 = redisUtil.getRedisTemplate().opsForValue().increment("id", -1);
        System.out.println(id2);

    }

    @Test
    public void testGet() {
        long seckillId = 1000;
        //调用service开启缓存
        Seckill seckillById = seckillService.getSeckillById(seckillId);

        System.out.println(redisUtil.getRedisTemplate().getKeySerializer());
        System.out.println(redisUtil.getRedisTemplate().getValueSerializer());
        redisUtil.getRedisTemplate().opsForValue().set("1", new Seckill());
        System.out.println(redisUtil.getRedisTemplate().opsForValue().get("1"));
        System.out.println(redisUtil.getRedisTemplate().hasKey("seckill_" + seckillId));


        //使用util方法查询
        Seckill seckill = (Seckill)redisUtil.getRedisTemplate().opsForValue().get("seckill_" + seckillId);
        System.out.println(seckill);


    }
}