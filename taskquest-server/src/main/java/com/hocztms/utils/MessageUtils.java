package com.hocztms.utils;


import com.hocztms.entity.MessageEntity;

public class MessageUtils {

    public static MessageEntity generateNoticeMessage(String openId,String content){
        return new MessageEntity(0,openId,"通知",content,0);
    }
}
