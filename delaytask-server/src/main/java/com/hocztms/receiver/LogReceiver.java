package com.hocztms.receiver;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hocztms.commons.Email;
import com.hocztms.entity.OperaLogEntity;
import com.hocztms.mqvo.OperaLogs;
import com.hocztms.service.LogService;
import com.hocztms.utils.EmailUtil;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class LogReceiver implements ChannelAwareMessageListener {


    @Autowired
    private LogService logService;


    @Override
    @RabbitListener(queues = "logQueue")
    public void onMessage(Message message, Channel channel) throws Exception {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            OperaLogs operaLogs = JSONObject.parseObject(new String(message.getBody()),OperaLogs.class);
            OperaLogEntity operaLogEntity = new OperaLogEntity();
            operaLogEntity.setLogId(0L);
            operaLogEntity.setOperaModule(operaLogs.getOperaModule());
            operaLogEntity.setOperaName(operaLogs.getOperaName());
            operaLogEntity.setAccount(operaLogs.getAccount());
            operaLogEntity.setAuthorities(operaLogs.getAuthorities());
            operaLogEntity.setUri(operaLogs.getUri());
            operaLogEntity.setIp(operaLogs.getIp());
            operaLogEntity.setReqParam(operaLogs.getReqParam());
            operaLogEntity.setResParam(operaLogs.getResParam());
            operaLogEntity.setOperaDate(operaLogs.getOperaDate());
            log.info("接收到的数据是:{}",operaLogEntity.toString());

            logService.insertOperaLog(operaLogEntity);
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