package com.hocztms.security.config;


import com.hocztms.security.entity.MyUserDetails;
import com.hocztms.security.servie.MyWxUserDetailServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@Slf4j
//自定义 微信登入逻辑
public class WxLoginAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private MyWxUserDetailServiceImpl userDetailService;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // TODO Auto-generated method stub
        String code = authentication.getName();// 这个获取表单输入中返回的用户名;
        if (userDetailService==null){
            System.out.println("okl");
        }


        MyUserDetails userInfo = (MyUserDetails) userDetailService.loadUserByUsername(code);
        return new WxLoginAuthenticationToken(userInfo, "vxLogin", userInfo.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {

        /**
         * providerManager会遍历所有
         * securityconfig中注册的provider集合
         * 根据此方法返回true或false来决定由哪个provider
         * 去校验请求过来的authentication
         */
        return (WxLoginAuthenticationToken.class
                .isAssignableFrom(authentication));
    }

}
