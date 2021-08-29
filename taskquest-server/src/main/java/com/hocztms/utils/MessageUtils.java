package com.hocztms.utils;


import com.hocztms.entity.MessageEntity;

public class MessageUtils {

    public static MessageEntity generateNoticeMessage(String openId,String content){
        return new MessageEntity(0,openId,"Tquest","通知",content,0);
    }

    public static MessageEntity generateNoticeMessage(String openId,String content,String from){
        return new MessageEntity(0,openId,from,"通知",content,0);
    }
}
