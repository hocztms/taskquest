package com.hocztms.config.aop;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented

public @interface OperaLog {
    String operaModule() default "unKnow"; // 操作模块
    String operaName() default "unKnow";  // 操作名
}
