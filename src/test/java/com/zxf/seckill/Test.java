package com.zxf.seckill;


import com.zxf.seckill.dto.SeckillExecution;
import com.zxf.seckill.dto.SeckillResult;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = "classpath:spring/spring-redis.xml")
public class Test {
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    private String name;

    @org.junit.Test
    public void test() {
        Assert.assertNotNull(redisTemplate);
        System.out.println(redisTemplate.getKeySerializer());
        System.out.println(redisTemplate.getValueSerializer());
        System.out.println(redisTemplate.getConnectionFactory().getClass());
    }

    @org.junit.Test
    public void test2() {
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));

        redisTemplate.opsForValue().set("id", 1);
        System.out.println(redisTemplate.opsForValue().get("id"));
        System.out.println(redisTemplate.opsForValue().increment("id", 1));
        System.out.println(redisTemplate.opsForValue().get("id"));

        redisTemplate.opsForValue().set("result", new SeckillResult<SeckillExecution>(false, "错误"));
        redisTemplate.opsForValue().get("result");
    }

}