package com.zxf.seckill.config;

import com.zxf.seckill.entity.RabbitMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = "classpath:spring/*.xml")
public class RabbitMQConfigTest {
    @Autowired
    RabbitTemplate rabbitTemplate;


    @Test
    public void test() {
        System.out.println(rabbitTemplate);
//        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, "template message");
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, new RabbitMessage(1,1,""));
    }

}