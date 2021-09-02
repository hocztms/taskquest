package com.hocztms.receiver;

import com.alibaba.fastjson.JSONObject;
import com.hocztms.entity.ExceptLogEntity;
import com.hocztms.entity.OperaLogEntity;
import com.hocztms.mqvo.ExceptLogs;
import com.hocztms.mqvo.OperaLogs;
import com.hocztms.service.LogService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExceptLogReciver implements ChannelAwareMessageListener {


    @Autowired
    private LogService logService;


    @Override
    @RabbitListener(queues = "exceptLogQueue")
    public void onMessage(Message message, Channel channel) throws Exception {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            ExceptLogs exceptLogs = JSONObject.parseObject(new String(message.getBody()),ExceptLogs.class);
            ExceptLogEntity exceptLogEntity = new ExceptLogEntity();
            exceptLogEntity.setExceptId(0L);
            exceptLogEntity.setAccount(exceptLogs.getAccount());
            exceptLogEntity.setAuthorities(exceptLogs.getAuthorities());
            exceptLogEntity.setUri(exceptLogs.getUri());
            exceptLogEntity.setIp(exceptLogs.getIp());
            exceptLogEntity.setReqParam(exceptLogs.getReqParam());
            exceptLogEntity.setExceptDate(exceptLogs.getExceptDate());
            exceptLogEntity.setExceptName(exceptLogs.getExceptName());
            exceptLogEntity.setExceptMsg(exceptLogs.getExceptMsg());

            log.info("接收到的数据是:{}",exceptLogEntity.toString());

            logService.insertExceptLog(exceptLogEntity);
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