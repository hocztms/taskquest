package com.hocztms.config.aop;

import com.hocztms.jwt.JwtTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;


@Aspect
@Component
@Slf4j
public class WebLogAspect {

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Pointcut("execution(* com.hocztms.controller.*.*(..))")
    private void weblogPointcut() {

    }

    @Before("weblogPointcut()")
    private void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();
        log.info("URL: " + request.getRequestURL().toString() + " HTTP_METHOD: " + request.getMethod() + " IP: " + request.getRemoteAddr() + " UserEntity: " + jwtTokenUtils.getAuthAccountFromToken(request.getHeader(jwtTokenUtils.getHeader())));
        log.info("data: " + Arrays.toString(joinPoint.getArgs()));

    }

    @AfterReturning(returning = "result", pointcut = "weblogPointcut()")
    public void doAfterReturning(Object result) {
        log.info("RESPONSE: " + result);
    }

}
