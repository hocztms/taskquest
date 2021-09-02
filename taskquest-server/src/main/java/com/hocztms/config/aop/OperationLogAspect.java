package com.hocztms.config.aop;

import com.hocztms.commons.RestResult;
import com.hocztms.mqvo.ExceptLogs;
import com.hocztms.mqvo.OperaLogs;
import com.hocztms.jwt.JwtTokenUtils;
import com.hocztms.security.entity.MyUserDetails;
import com.hocztms.service.impl.RabbitmqService;
import com.hocztms.utils.AuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
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
    private AuthUtils authUtils;

    @Autowired
    private RabbitmqService rabbitmqService;

    @Pointcut("@annotation(com.hocztms.config.aop.OperaLog)")
    private void OperaLogPointCut() {

    }

    @Pointcut("execution(* com.hocztms.controller.*.*(..))")
    public void ExceptionLogPointCut() {
    }

    @AfterReturning(value = "OperaLogPointCut()",returning = "result")
    public void saveOperaLog(JoinPoint joinPoint,Object result) {
        RestResult restResult = (RestResult) result;
        if (restResult.getCode()==-1){
            return;
        }
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
        operaLogs.setCollegeId(userDetails.getCollegeId());


        //反射机制
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperaLog operaLog = method.getAnnotation(OperaLog.class);


        operaLogs.setOperaModule(operaLog.operaModule());
        operaLogs.setOperaName(operaLog.operaName());

        operaLogs.setReqParam(Arrays.toString(joinPoint.getArgs()));
        operaLogs.setResParam(result.toString());

        operaLogs.setOperaDate(new Date());


        rabbitmqService.insertOperaLog(operaLogs);
    }


    @AfterReturning(value = "ExceptionLogPointCut()",returning ="result" )
    public void saveExceptLog(JoinPoint joinPoint,Object result) {
        RestResult restResult = (RestResult) result;
        if (restResult.getCode()!=-1){
            return;
        }
        Throwable e = (Throwable) restResult.getData();
        log.info("saveExceptLog 执行了。。。");
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();


        //工具类获取security 上下文
        MyUserDetails userDetails = authUtils.getContextUserDetails();

        ExceptLogs exceptLogs = new ExceptLogs();

        exceptLogs.setAccount(userDetails.getUsername());
        exceptLogs.setAuthorities(userDetails.getAuthorities().toString());
        exceptLogs.setIp(request.getRemoteAddr());
        exceptLogs.setUri(request.getRequestURI());
        exceptLogs.setExceptDate(new Date());
        exceptLogs.setExceptName(e.getClass().getName());
        exceptLogs.setExceptMsg(e.getClass().getName() + " : " +e.getMessage() + " : " + e.getStackTrace());
        exceptLogs.setReqParam(Arrays.toString(joinPoint.getArgs()));


        rabbitmqService.insertExceptLog(exceptLogs);
    }

}