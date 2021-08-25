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

    /*
     * 用于绑定到直连交换机的队列
     */
    public static final String EMAIL_DIRECT_QUEUE = "emailQueue";

    /**
     * 声明直连交换机
     */
    public static final String DIRECT_EXCHANGE = "directExchange";

    /**
     * 声明关于直连交换机TEST_DIRECT_EXCHANGE的routing-key :
     */
    public static final String EMAIL_DIRECT_ROUTING = "emailRouting";

    /**
     * 声明直连交换机 这个交换机不绑定任何队列
     */
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
    public Queue testDirectQueue() {
        //true 是否持久
        return new Queue(EMAIL_DIRECT_QUEUE,true);
    }

    /**
     * Direct交换机 起名：TestDirectExchange  直连交换机,需要双方遵守routingKey
     * @return
     */
    @Bean
    DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE);
    }

    /**
     * 绑定  将队列和交换机绑定, 并设置用于匹配键：TestDirectRouting
     * @return
     */
    @Bean
    Binding bindingDirect() {
        return BindingBuilder.bind(testDirectQueue()).to(directExchange()).with(EMAIL_DIRECT_ROUTING);
    }

    /**
     * Direct交换机 起名：LonelyDirectExchange  模拟生产者发送消息时找不到交换机的情形
     * @return
     */
    @Bean
    DirectExchange testExchangeNoQueueExit() {
        return new DirectExchange(TEST_DIRECT_EXCHANGE_NO_HAVE__QUEUE);
    }

}
