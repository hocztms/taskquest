package com.hocztms.service;

import com.hocztms.commons.RestResult;
import com.hocztms.entity.MessageEntity;

import java.util.List;
import java.util.Map;

public interface AuthMessageService {

    RestResult userGetMessage(String openId);

    RestResult userDeleteMessageById(List<Long> ids, String openId);

    RestResult userReadMessage(List<Long> ids,String openId);

    void sendCollegeAdminMessage(String username,String message);

    void sendUserMessage(String openId, MessageEntity messageEntity);

    void sendCollegeAdminTaskProgress(String username, Map map);
}
