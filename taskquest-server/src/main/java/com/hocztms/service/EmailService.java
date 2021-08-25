package com.hocztms.service;

import com.hocztms.commons.RestResult;
import com.hocztms.vo.EmailVo;

public interface EmailService {

    RestResult sendUserEmailBindCode(EmailVo emailVo,String openId);
}
