package com.hocztms.service.impl;

import com.hocztms.commons.Email;
import com.hocztms.commons.RestResult;
import com.hocztms.service.EmailService;
import com.hocztms.service.UserService;
import com.hocztms.utils.CodeUtils;
import com.hocztms.utils.ResultUtils;
import com.hocztms.vo.EmailVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired
    private RedisService redisService;
    @Autowired
    private UserService userService;
    @Autowired
    private CodeUtils codeUtils;
    @Autowired
    private RabbitmqService rabbitmqService;
    @Override
    public RestResult sendUserEmailBindCode(EmailVo emailVo,String openId) {
       try {
           if (redisService.getEmailCode(openId) != null) {
               return ResultUtils.error("请勿重复操作");
           }


           String code = codeUtils.generateCode();
           log.info("用户:{}   本次邮箱验证码:{}", openId, code);

           Email email = new Email(emailVo.getEmail(), "邮箱绑定验证码", "本次验证码为" + code + "三分钟有效", new Date(), code);
           redisService.insertEmailCode(openId, email);


           List<Email> emails = new ArrayList<>();
           emails.add(email);
           rabbitmqService.sendEmailList(emails);
           return ResultUtils.success();
       }catch (Exception e){
           e.printStackTrace();
           return ResultUtils.systemError(e);
       }
    }
}
