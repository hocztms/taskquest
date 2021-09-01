package com.hocztms.receiver;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hocztms.commons.Email;
import com.hocztms.utils.EmailUtil;
import com.hocztms.utils.RabbitMessageUtils;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;

@Slf4j
@Component
public class EmailReceiver implements ChannelAwareMessageListener {

    @Autowired
    private EmailUtil emailUtil;
    @Override
    @RabbitListener(queues = "emailQueue")
    public void onMessage(Message message, Channel channel) throws Exception {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            List<Email> emailList = JSONArray.parseArray(new String(message.getBody()),  Email.class);
            log.info("接收到的数据是:{}",emailList.toString());


            for (Email email :emailList){
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            emailUtil.sendEmail(email);
                        }catch (Exception e){
                            throw new RuntimeException(email.getTo() + "发送失败。。。");
                        }
                    }
                });
                thread.start();
            }



            //为true表示确认之前的所有消息  false表示只来处理着当前的消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("处理消息时显示异常,异常是:{},现拒绝消费当前消息且不再放回队列",e.getMessage());
            //为true会重新放回队列
            channel.basicReject(deliveryTag, false);
        }
    }
}
