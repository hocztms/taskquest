package com.hocztms.utils;

import com.alibaba.fastjson.JSONObject;
import com.hocztms.commons.Email;
import org.springframework.amqp.core.Message;

public class RabbitMessageUtils {

    public static String objToString(Object message){
        return JSONObject.toJSONString(message);
    }

    public static Email messageToEmail(Message message){
        return JSONObject.parseObject(new String(message.getBody()),Email.class);
    }
}
