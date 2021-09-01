package com.hocztms.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageListenerConfig {

    private final CachingConnectionFactory connectionFactory;

    @Autowired
    public MessageListenerConfig(
            CachingConnectionFactory connectionFactory
    ) {
        this.connectionFactory = connectionFactory;
    }

    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        //设置当前并发消费者的数量
        container.setConcurrentConsumers(100);
        //设置最大并发消费者的数量
        container.setMaxConcurrentConsumers(100);
        // RabbitMQ默认是自动确认，这里改为手动确认消息
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);

        return container;
    }
}
