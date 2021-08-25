package com.hocztms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hocztms.commons.Email;
import com.hocztms.commons.RestResult;
import com.hocztms.dto.UserDto;
import com.hocztms.entity.*;
import com.hocztms.jwt.JwtAuthService;
import com.hocztms.jwt.JwtTokenUtils;
import com.hocztms.mapper.StudentMapper;
import com.hocztms.mapper.admin.AdminMapper;
import com.hocztms.mapper.admin.AdminRoleMapper;
import com.hocztms.mapper.user.UserMapper;
import com.hocztms.mapper.user.UserRoleMapper;
import com.hocztms.service.AuthService;
import com.hocztms.service.UserService;
import com.hocztms.utils.ResultUtils;
import com.hocztms.vo.AdminVo;
import com.hocztms.vo.UserAuthVo;
import com.hocztms.vo.WxUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private JwtAuthService jwtAuthService;
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private AdminRoleMapper adminRoleMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private JwtTokenUtils tokenUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisService redisService;


    @Override
    public RestResult adminLogin(AdminVo adminVo) {

        return jwtAuthService.adminLogin(adminVo.getUsername(), adminVo.getPassword());
    }

    @Override
    public RestResult wxUserLogin(WxUserVo wxUserVo) {
        return jwtAuthService.wxUserLogin(wxUserVo.getCode());
    }

    @Override
    public RestResult userBindEmail(String code,String openId) {
        try {
            Email emailCode = redisService.getEmailCode(openId);
            if (emailCode == null){
                return ResultUtils.error("请先获取验证码");
            }
            System.out.println(emailCode.getCode() + code);
            if (!emailCode.getCode().equals(code)){
                return ResultUtils.error("验证码错误。。。");
            }

            UserEntity userByOpenId = userService.findUserByOpenId(openId);

            userByOpenId.setEmail(emailCode.getTo());

            userService.updateUserByOpenId(userByOpenId);
            return ResultUtils.success();
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtils.systemError();
        }
    }

    @Override
    public RestResult checkUserAuth(String openId) {
        try {
            if (openId==null){
                return ResultUtils.error("未登录");
            }
            List<UserRoleEntity> userRolesByOpenId = userService.findUserRolesByOpenId(openId);
            if (!userRolesByOpenId.contains(new UserRoleEntity(openId,"student"))) {
                return ResultUtils.error("未认证");
            }
            return ResultUtils.success();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.systemError();
        }
    }

    @Override
    public RestResult userDoAuth(UserAuthVo authVo, String openId) {
        try {

            List<UserRoleEntity> userRolesByOpenId = userService.findUserRolesByOpenId(openId);
            if (userRolesByOpenId.contains(new UserRoleEntity(openId,"student"))) {
                return ResultUtils.error("用户已经认证");
            }

            UserEntity userEntityByStudentId = userService.findUserEntityByStudentId(authVo.getStudentId());

            if (userEntityByStudentId != null) {
                return ResultUtils.error("该学号已认证");
            }

            StudentEntity student = userService.findStudentByStudentId(authVo.getStudentId().intValue());
            if (student == null) {
                return ResultUtils.error("学号错误");
            }
            if (!passwordEncoder.matches(authVo.getPassword(), student.getPassword())) {
                return ResultUtils.error("密码错误");
            }


            UserEntity userEntity = userService.findUserByOpenId(openId);
            userEntity.setStudentId(student.getStudentId());
            userEntity.setStatus(1);
            userService.updateUserByOpenId(userEntity);
            userService.insertUserRole(new UserRoleEntity(openId, "student"));

            return ResultUtils.success();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.systemError();
        }
    }

    @Override
    public RestResult userGetInfo(String openId) {
        try {
            UserDto userDto = userService.findUserDtoByOpenId(openId);

            return ResultUtils.success(userDto);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtils.systemError();
        }
    }



}
