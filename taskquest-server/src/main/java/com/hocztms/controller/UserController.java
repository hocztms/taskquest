package com.hocztms.controller;

import com.alibaba.fastjson.JSONObject;
import com.hocztms.commons.RestResult;
import com.hocztms.jwt.JwtAuthService;
import com.hocztms.service.AuthService;
import com.hocztms.service.EmailService;
import com.hocztms.vo.EmailVo;
import com.hocztms.vo.UserAuthVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/user")
@PreAuthorize("hasAuthority('user')")
@Api(tags = "用户方法接口，权限要求：用户")
public class UserController {

    @Autowired
    private JwtAuthService jwtAuthService;
    @Autowired
    private AuthService authService;
    @Autowired
    private EmailService emailService;


    //认证接口
    @PostMapping("/userDoAuth")
    public RestResult userDoAuth(@Validated @RequestBody UserAuthVo authVo, HttpServletRequest request){
        String account = jwtAuthService.getAccountFromToken(request);
        return authService.userDoAuth(authVo,account);
    }


    @PostMapping("/sendEmailBindCode")
    public RestResult sendEmailBindCode(@Validated @RequestBody EmailVo emailVo,HttpServletRequest request){
        String account = jwtAuthService.getAccountFromToken(request);
        return emailService.sendUserEmailBindCode(emailVo,account);
    }

    @PutMapping("/bindEmail")
    public RestResult sendEmailBindCode(
            @ApiParam(value = "格式 {\"code\":\"code\"}") @RequestBody String json, HttpServletRequest request){
        String code = (String) JSONObject.parseObject(json).get("code");
        String account = jwtAuthService.getAccountFromToken(request);
        return authService.userBindEmail(code,account);
    }

    @GetMapping("/getUserInfo")
    public RestResult getUserInfo(HttpServletRequest request){

        return authService.userGetInfo(jwtAuthService.getAccountFromToken(request));
    }

}
