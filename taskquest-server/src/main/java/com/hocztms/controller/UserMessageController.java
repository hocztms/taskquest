package com.hocztms.controller;


import com.hocztms.commons.RestResult;
import com.hocztms.jwt.JwtAuthService;
import com.hocztms.service.AuthMessageService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/user")
@PreAuthorize("hasAuthority('student')")

@Api(tags = "用户信息方法接口，权限要求：学生")

public class UserMessageController {

    @Autowired
    private JwtAuthService jwtAuthService;
    @Autowired
    private AuthMessageService authMessageService;

    @GetMapping("/getMessage")
    public RestResult getUserMessage(HttpServletRequest request){
        String openId = jwtAuthService.getAccountFromToken(request);
        return authMessageService.userGetMessage(openId);
    }

    @DeleteMapping("/deleteMessage")
    public RestResult deleteUserMessage(@RequestBody List<Long> messageIds, HttpServletRequest request){
        String openId = jwtAuthService.getAccountFromToken(request);

        return authMessageService.userDeleteMessageById(messageIds,openId);
    }

    @PutMapping("/readMessage")
    public RestResult readUserMessage(@RequestBody List<Long> messageIds,HttpServletRequest request){
        String openId = jwtAuthService.getAccountFromToken(request);
        return authMessageService.userReadMessage(messageIds,openId);
    }
}
