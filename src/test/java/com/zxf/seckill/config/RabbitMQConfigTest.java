package com.zxf.seckill.config;

import org.junit.Test;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(value = "classpath:spring/spring-redis.xml")
public class RabbitMQConfigTest {
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    ConnectionFactory connectionFactory;

    @Autowired
    RedisTemplate redisTemplate;


    @Test
    public void test() {
        ApplicationContext context = new AnnotationConfigApplicationContext(RabbitMQConfig.class);
        RabbitTemplate rabbitTemplate = (RabbitTemplate)context.getBean("rabbitTemplate");
        System.out.println(rabbitTemplate);
    }
}