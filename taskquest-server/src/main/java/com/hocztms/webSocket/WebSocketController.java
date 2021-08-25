package com.hocztms.webSocket;

//import com.hocztms.service.UserMessageService;
//import com.hocztms.jwt.JwtAuthService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;

//@RestController
//@RequestMapping("/socket")
///*
//websocket测试
// */
//public class WebSocketController {
//
//    @Autowired
//    private UserMessageService userMessageService;
//    @Autowired
//    private JwtAuthService jwtAuthService;
//    @Autowired
//    private WebSocketServer webSocketServer;


//    @PostMapping(value = "/sendMsg")
//    public void testSocket1(@RequestBody String msg,HttpServletRequest request) {
//        String username = jwtAuthService.getTokenUsername(request);
//        System.out.println(username);
//        userMessageService.sendUsersMessage(username, MessageUtils.generateCompanyMessage());
//    }
//
//    @PostMapping(value = "/test")
//    public void testSocket1(HttpServletRequest request) {
//        webSocketServer.onClose(request.getHeader("token"));
//    }
//}