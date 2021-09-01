package com.hocztms.config;



import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
public class RabbitMqConfig {

    public static final String EMAIL_DIRECT_QUEUE = "emailQueue";


    public static final String LOG_DIRECT_QUEUE = "logQueue";

    public static final String DIRECT_EXCHANGE = "directExchange";


    public static final String EMAIL_DIRECT_ROUTING = "emailRouting";

    public static final String LOG_DIRECT_ROUTING = "logRouting";

    //测试
    public static final String TEST_DIRECT_EXCHANGE_NO_HAVE__QUEUE = "LonelyExchange";


    @Bean
    public RabbitTemplate createRabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);


        //设置开启Mandatory,才能触发回调函数,无论消息推送结果怎么样都强制调用回调函数
        rabbitTemplate.setMandatory(true);

        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                log.info("ConfirmCallback相关数据：{}",correlationData);
                log.info("ConfirmCallback确认情况：{}",ack);
                log.info("ConfirmCallback原因：{}",cause);
            }
        });

        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                log.info("ReturnCallback消息：{}",message);
                log.info("ReturnCallback回应码：{}",replyCode);
                log.info("ReturnCallback回应信息：{}",replyText);
                log.info("ReturnCallback交换机：{}",exchange);
                log.info("ReturnCallback路由键：{}",routingKey);
            }
        });

        return rabbitTemplate;
    }
    @Bean
    public Queue emailDirectQueue() {
        return new Queue(EMAIL_DIRECT_QUEUE,true);
    }

    @Bean
    public Queue logDirectQueue(){
        log.info("执行力，sdldajkshdkashkdhaskj");
        return new Queue(LOG_DIRECT_QUEUE,true);
    }


    @Bean
    DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE);
    }


    @Bean
    Binding bindingEmailDirect() {
        return BindingBuilder.bind(emailDirectQueue()).to(directExchange()).with(EMAIL_DIRECT_ROUTING);
    }

    @Bean
    Binding bindingLogDirect() {
        return BindingBuilder.bind(logDirectQueue()).to(directExchange()).with(LOG_DIRECT_ROUTING);
    }


    @Bean
    DirectExchange testExchangeNoQueueExit() {
        return new DirectExchange(TEST_DIRECT_EXCHANGE_NO_HAVE__QUEUE);
    }

}
