package com.hocztms.service.impl;

import com.alibaba.fastjson.JSON;
import com.hocztms.commons.Email;
import com.hocztms.config.RabbitMqConfig;
import com.hocztms.mqvo.ExceptLogs;
import com.hocztms.mqvo.OperaLogs;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RabbitmqService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendEmail(Email email){
        rabbitTemplate.convertAndSend(RabbitMqConfig.DIRECT_EXCHANGE,RabbitMqConfig.EMAIL_DIRECT_ROUTING, JSON.toJSONString(email));
    }

    public void sendEmailList(List<Email> email){
        rabbitTemplate.convertAndSend(RabbitMqConfig.DIRECT_EXCHANGE,RabbitMqConfig.EMAIL_DIRECT_ROUTING, JSON.toJSONString(email));
    }

    public void insertOperaLog(OperaLogs operaLogs){
        rabbitTemplate.convertAndSend(RabbitMqConfig.DIRECT_EXCHANGE,RabbitMqConfig.OPERA_LOG_DIRECT_ROUTING,JSON.toJSONString(operaLogs));
    }

    public void insertExceptLog(ExceptLogs exceptLogs){
        System.out.println("insertExceptLog...");
        rabbitTemplate.convertAndSend(RabbitMqConfig.DIRECT_EXCHANGE,RabbitMqConfig.EXCEPT_LOG_DIRECT_ROUTING,JSON.toJSONString(exceptLogs));
    }
}
