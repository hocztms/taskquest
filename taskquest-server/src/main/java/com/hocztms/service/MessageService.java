package com.hocztms.service;

import com.hocztms.entity.MessageEntity;

import java.util.List;

public interface MessageService {

    MessageEntity insertMessage(MessageEntity messageEntity);

    MessageEntity updateMessageById(MessageEntity messageEntity);

    void deleteMessageById(MessageEntity messageEntity);

    MessageEntity findMessageById(Long id);

    List<MessageEntity> findMessageByOpenId(String openId);
}
