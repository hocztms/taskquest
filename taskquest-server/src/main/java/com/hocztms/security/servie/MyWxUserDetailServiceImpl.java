package com.hocztms.security.servie;

import com.hocztms.entity.UserEntity;
import com.hocztms.entity.UserRoleEntity;
import com.hocztms.security.entity.MyUserDetails;
import com.hocztms.service.UserService;
import com.hocztms.utils.WxUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MyWxUserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private WxUtils wxUtils;

    @Autowired
    private UserService userService;


    @Override
    public UserDetails loadUserByUsername(String code) throws UsernameNotFoundException {
        String openId =null;

        //获取openId
        try {
            openId = wxUtils.getOpenIdByCode(code);
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("无效code");
        }
        UserEntity user = userService.findUserByOpenId(openId);


        if (user==null){
            userService.insertUser(new UserEntity(openId,null,null,null,0,0,0));
        }

        List<UserRoleEntity> userRoleEntities = userService.findUserRolesByOpenId(openId);

        List<GrantedAuthority> authoritys = new ArrayList<>();

        for (UserRoleEntity userRoleEntity:userRoleEntities){
            authoritys.add(new SimpleGrantedAuthority(userRoleEntity.getRole()));
        }

        return new MyUserDetails(openId,"vxLogin",authoritys);

    }
}
