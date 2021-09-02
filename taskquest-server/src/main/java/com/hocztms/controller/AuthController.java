package com.hocztms.controller;

import com.hocztms.commons.RestResult;
import com.hocztms.jwt.JwtAuthService;
import com.hocztms.service.impl.RedisService;
import com.hocztms.service.AuthService;
import com.hocztms.utils.RedisLockUtil;
import com.hocztms.utils.ResultUtils;
import com.hocztms.vo.AdminVo;
import com.hocztms.vo.WxUserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;

@RestController
@RequestMapping("/auth")
@PreAuthorize("permitAll()")
@Api(tags = "用户基础接口，权限要求：无")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private JwtAuthService jwtAuthService;

    @Autowired
    private RedisLockUtil redisLockUtil;

    @ApiOperation("管理员登入接口 超级管理员返回code 2 学院管理员 code 1")
    @PostMapping("/adminLogin")
    public RestResult adminLogin(@RequestBody @Validated AdminVo adminVo){
        if(!redisService.checkUserLoginLimit(adminVo.getUsername())){
            return ResultUtils.error("登入失败过多稍后再试");
        }

        return authService.adminLogin(adminVo);
    }


    @ApiOperation("微信小程序登入接口")
    @PostMapping("/wxUserLogin")
    public RestResult vxUserLogin(@RequestBody @Validated WxUserVo wxUserVo){
        return authService.wxUserLogin(wxUserVo);
    }

    @ApiOperation("小程序身份绑定测试接口")
    @GetMapping("/checkUserAuth")
    public RestResult checkUserAuth(HttpServletRequest request){
        String account = jwtAuthService.getAccountFromToken(request);
        return authService.checkUserAuth(account);
    }

    @ApiOperation("登出接口")
    @PostMapping("/logout")
    public RestResult logout(HttpServletRequest request){

        return authService.userLogout(request);
    }

//    @GetMapping("/testLock")
//    public void testRedisLock(){
//        Integer i = new Random().nextInt(20);
//        redisLockUtil.grabTaskLock(11L, new String(i.toString()));
//    }
}
