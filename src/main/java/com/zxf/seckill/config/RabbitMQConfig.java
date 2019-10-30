package com.zxf.seckill.config;

import com.zxf.seckill.mq.RabbitMQReceiver;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类
 * 这里使用work queues消息模型：一个队列，两个消费者
 * Spring AMQP默认实现了:
 * 1)队列/消息持久化
 * 2）手动应答
 * 3）能者多劳
 */
@Configuration
public class RabbitMQConfig {
    public static final String QUEUE_NAME = "seckill_queue";

    //创建连接工厂
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
//        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        return connectionFactory;
    }

    //向RabbitMQ发送消息时，使用json格式的序列化方式
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    //定义消息模板
    @Bean(name="rabbitTemplate")
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        template.setMessageConverter(messageConverter());
        return template;
    }

    //创建队列：在该Bean加入Spring容器时就创建该队列
    @Bean
    public Queue seckillQueue() {
        return new Queue(QUEUE_NAME);
    }

    //创建消费者1
    @Bean
    public RabbitMQReceiver receiver1() {
        return new RabbitMQReceiver();
    }

    //创建消费者2
    @Bean
    public RabbitMQReceiver receiver2() {
        return new RabbitMQReceiver();
    }
}
