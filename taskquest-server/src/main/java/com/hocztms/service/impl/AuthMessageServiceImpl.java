package com.hocztms.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hocztms.commons.RestResult;
import com.hocztms.entity.MessageEntity;
import com.hocztms.service.AuthMessageService;
import com.hocztms.service.MessageService;
import com.hocztms.utils.ResultUtils;
import com.hocztms.vo.SocketMessage;
import com.hocztms.webSocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AuthMessageServiceImpl implements AuthMessageService {
    @Autowired
    private WebSocketServer webSocketServer;
    @Autowired
    private MessageService messageService;

    @Override
    public RestResult userGetMessage(String openId) {
        try {
            List<MessageEntity> messageByOpenId = messageService.findMessageByOpenId(openId);

            return ResultUtils.success(messageByOpenId);

        }catch (Exception e){
            e.printStackTrace();
            return ResultUtils.systemError();
        }
    }

    @Override
    public RestResult userDeleteMessageById(List<Long> ids,String openId) {
        try {
            for (Long id:ids){
                MessageEntity message = messageService.findMessageById(id);
                if (message==null){
                    continue;
                }

                if(!message.getOpenId().equals(openId)){
                    return ResultUtils.error("无权限");
                }

                messageService.deleteMessageById(message);
            }

            return ResultUtils.success();
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtils.systemError();
        }
    }

    @Override
    public RestResult userReadMessage(List<Long> ids, String openId) {
        try {
            for (Long id:ids){
                MessageEntity message = messageService.findMessageById(id);
                if (message==null){
                    continue;
                }

                if(!message.getOpenId().equals(openId)){
                    return ResultUtils.error("无权限");
                }

                if (message.getReadTag()==1){
                    return ResultUtils.error("请勿重复操作");
                }
                message.setReadTag(1);
                messageService.updateMessageById(message);
            }

            return ResultUtils.success();
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtils.systemError();
        }
    }

    @Override
    public void sendCollegeAdminMessage(String username, String message) {
        webSocketServer.sendInfo(username,JSONObject.toJSONString(new SocketMessage(0,message)));
    }

    @Override
    public void sendUserMessage(String openId, MessageEntity messageEntity) {
        webSocketServer.sendInfo(openId, JSONObject.toJSONString(new SocketMessage(0,messageEntity)));
        messageService.insertMessage(messageEntity);
    }

    @Override
    public void sendCollegeAdminTaskProgress(String username, Map map) {
        webSocketServer.sendInfo(username,JSONObject.toJSONString(new SocketMessage(1,map)));
    }
}
