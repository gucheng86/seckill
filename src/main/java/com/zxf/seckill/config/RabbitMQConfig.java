package com.zxf.seckill.config;

import com.zxf.seckill.mq.RabbitMQReceiver;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
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
    public static final String QUEUE_NAME = "seckillQueue";

    //创建连接工厂
    @Bean
    public CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses("localhost:5672");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/");
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

    //通过指定admin信息，当前生产的exchange和queue信息会在admin自动生成
    @Bean(name="admin")
    public RabbitAdmin admin() {
        return new RabbitAdmin(connectionFactory());
    }

    //创建队列：在该Bean加入Spring容器时就创建该队列
    @Bean
    public Queue seckillQueue() {
        return new Queue(QUEUE_NAME);
    }


    //配合@RabbitListener使用
    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);

        //设置消费者线程数
        factory.setConcurrentConsumers(5);
        //设置消费者最大线程数
        factory.setMaxConcurrentConsumers(10);

        //设置序列方式
        factory.setMessageConverter(messageConverter());

        return factory;
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
