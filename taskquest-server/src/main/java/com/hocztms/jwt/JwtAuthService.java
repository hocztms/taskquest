package com.hocztms.jwt;

import com.hocztms.commons.RestResult;
import com.hocztms.service.impl.RedisService;
import com.hocztms.security.config.WxLoginAuthenticationToken;
import com.hocztms.security.entity.MyUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@Slf4j
public class JwtAuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Autowired
    private RedisService redisService;


    public RestResult adminLogin(String username, String password) {
        Authentication authentication;
        try {
            // 进行身份验证,
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {

            redisService.setUserLoginLimit(username);
            return new RestResult(0, e.getMessage(), null);
        }

        MyUserDetails loginUser = (MyUserDetails) authentication.getPrincipal();
        RestResult result = new RestResult(1, "登入成功", null);

        log.info("管理员:{} 已经登入。。。本次权限为:{}", loginUser.getUsername(), loginUser.getAuthorities().toString());

        if (loginUser.getAuthorities().contains(new SimpleGrantedAuthority("superAdmin"))){
            result.setCode(2);
        }
        //主动失效 设置黑名单 并关闭已存在socket
        if (redisService.userLogoutByServer(username) == 0) {
            return null;
        }

        result.put("token", jwtTokenUtils.generateToken(loginUser, "admin"));
        return result;
    }

    public RestResult wxUserLogin(String code) {
        Authentication authentication;
        try {
            // 进行身份验证,
            authentication = authenticationManager.authenticate(
                    new WxLoginAuthenticationToken(code, "vxLogin"));
        } catch (Exception e) {
            e.printStackTrace();
            return new RestResult(0, e.getMessage(), null);
        }

        MyUserDetails loginUser = (MyUserDetails) authentication.getPrincipal();
        RestResult result = new RestResult(1, "登入成功", null);

        log.info("用户:{} 已经登入。。。本次权限为:{}", loginUser.getUsername(), loginUser.getAuthorities().toString());


        //主动失效 设置黑名单 并关闭已存在socket
        if (redisService.userLogoutByServer(loginUser.getUsername()) == 0) {
            return null;
        }

        result.put("token", jwtTokenUtils.generateToken(loginUser, "user"));
        return result;
    }


    public String getToken(HttpServletRequest request) {
        return request.getHeader(jwtTokenUtils.getHeader());
    }


    public String getAccountFromToken(HttpServletRequest request) {
        return jwtTokenUtils.getAuthAccountFromToken(request.getHeader(jwtTokenUtils.getHeader()));
    }


}
