package com.hocztms.config.aop;

import com.hocztms.mqvo.OperaLogs;
import com.hocztms.jwt.JwtTokenUtils;
import com.hocztms.security.entity.MyUserDetails;
import com.hocztms.utils.AuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;

@Aspect
@Component
@Slf4j
public class OperationLogAspect {

    @Autowired
    private JwtTokenUtils jwtTokenUtils;
    @Autowired
    private AuthUtils authUtils;

    @Pointcut("@annotation(com.hocztms.config.aop.OperaLog)")
    private void OperaLogPointCut() {

    }

    @AfterReturning(value = "OperaLogPointCut()",returning = "result")
    public void saveOperaLog(JoinPoint joinPoint,Object result) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();


        //工具类获取security 上下文
        MyUserDetails userDetails = authUtils.getContextUserDetails();

        OperaLogs operaLogs = new OperaLogs();

        operaLogs.setAccount(userDetails.getUsername());
        operaLogs.setAuthorities(userDetails.getAuthorities().toString());
        operaLogs.setIp(request.getRemoteAddr());
        operaLogs.setUri(request.getRequestURI());


        //反射机制
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperaLog operaLog = method.getAnnotation(OperaLog.class);


        operaLogs.setOperaModule(operaLog.operaModule());
        operaLogs.setOperaName(operaLog.operaName());

        operaLogs.setReqParam(Arrays.toString(joinPoint.getArgs()));
        operaLogs.setResParam(result.toString());

        operaLogs.setOperaDate(new Date());
        System.out.println(operaLogs.toString());
    }

}