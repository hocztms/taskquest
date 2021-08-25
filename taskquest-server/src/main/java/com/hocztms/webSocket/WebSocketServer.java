package com.hocztms.webSocket;

import com.alibaba.fastjson.JSON;
import com.hocztms.jwt.JwtTokenUtils;
import com.hocztms.vo.SocketMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@ServerEndpoint(value = "/socket/{token}")
@Slf4j
public class WebSocketServer {


    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static AtomicInteger online = new AtomicInteger();

    //concurrent包的线程安全Set，用来存放每个客户端对应的WebSocketServer对象。
    private static Map<String, Session> sessionPools = new HashMap<>();



    private static JwtTokenUtils jwtTokenUtils;

    @Autowired
    public void setChatService(JwtTokenUtils jwtTokenUtils) {
        WebSocketServer.jwtTokenUtils = jwtTokenUtils;
    }




    /*
    发送消息
     */
    public void sendMessage(Session session, String message) throws IOException{
        if(session != null){
            session.getBasicRemote().sendText(message);
        }
    }


   /*
   建立连接
    */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "token") String token){
        System.out.println(token);
        try {
            if (!jwtTokenUtils.checkRedisBlack(token)){
                log.warn(token + "  无效token....");
                throw new RuntimeException("无效token");
            }
        }catch (Exception e){
            e.printStackTrace();
            log.warn(token + "  无效token....");
            throw new RuntimeException("无效token");
        }
        String userName = jwtTokenUtils.getAuthAccountFromToken(token);

        log.info(userName + "准备进入连接.....");

        //如果已经存在连接 则删除
        if (sessionPools.get(userName)!=null){
            sessionPools.remove(userName);
        }

        else {
            addOnlineCount();
        }

        sessionPools.put(userName, session);
        log.info(userName + "加入webSocket！当前人数为" + online);
        try {
            sendMessage(session, "欢迎" + userName + "加入连接！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    关闭连接
     */
    @OnClose
    public void onClose(@PathParam(value = "token") String token){
        //token鉴权...
        String username = jwtTokenUtils.getAuthAccountFromToken(token);

        close(username);
    }

    public void close(String username) {
        Session session = sessionPools.get(username);

        if (session!=null&&session.isOpen()){
           try {
               onMessage(session, JSON.toJSONString(new SocketMessage(-1,username+"连接已断开")));
               sessionPools.remove(username);
               subOnlineCount();
           }catch (Exception e){
               log.warn(username+"登出错误.....");
               e.printStackTrace();
           }
            log.info(username + "断开webSocket连接！当前人数为" + online);
        }
    }

    /*
    收到前端消息处理
     */
    @OnMessage
    public void onMessage(Session session,String message) throws IOException{
        System.out.println(message);
        sendMessage(session,"这边建议直接加vx聊天");
    }

    /*
    错误处理
     */
    @OnError
    public void onError(Session session, Throwable throwable){
        System.out.println("发生错误");
        throwable.printStackTrace();
    }

    /*
    向指定用户发送
     */
    public void sendInfo(String userName, String message){
        Session session = sessionPools.get(userName);
        if (session==null){
            log.warn(userName + "  未登录.....");
            return;
        }
        try {
            sendMessage(session, message);
            log.info(message + " 发送给 " + userName + " 成功....." );
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void addOnlineCount(){
        online.incrementAndGet();
    }

    public static void subOnlineCount() {
        online.decrementAndGet();
    }
}
