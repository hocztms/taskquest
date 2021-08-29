package com.hocztms.service;

import com.hocztms.commons.RestResult;
import com.hocztms.commons.RestResult;
import com.hocztms.entity.AdminEntity;
import com.hocztms.entity.AdminRoleEntity;
import com.hocztms.entity.StudentEntity;
import com.hocztms.vo.AdminVo;
import com.hocztms.vo.EmailVo;
import com.hocztms.vo.UserAuthVo;
import com.hocztms.vo.WxUserVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface AuthService {

    RestResult adminLogin(AdminVo adminVo);

    RestResult wxUserLogin(WxUserVo wxUserVo);

    RestResult userBindEmail(String code,String openId);

    RestResult checkUserAuth(String openId);

    RestResult userDoAuth(UserAuthVo authVo, String openId);

    RestResult userGetInfo(String openId);

    RestResult userLogout(HttpServletRequest request);

}
