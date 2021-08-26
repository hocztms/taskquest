package com.hocztms.config.cacheResolvers;

import com.hocztms.entity.TaskRecords;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.SimpleCacheResolver;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@Slf4j
public class UserTaskRecordsCacheResolver extends SimpleCacheResolver implements CacheResolver {

    public static final String CollegeTaskGroup = "userRecords";


    public UserTaskRecordsCacheResolver(CacheManager cacheManager) {
        super(cacheManager);
    }


    @Override
    protected Collection<String> getCacheNames(CacheOperationInvocationContext<?> context) {
        Object arg = Arrays.stream(context.getArgs()).findFirst().get();
        String openId = null;
        if (arg instanceof String) {
            openId = (String) arg;
        } else if (arg instanceof TaskRecords) {
            openId = ((TaskRecords) arg).getOpenId();
        }
        return Collections.singleton(CollegeTaskGroup + "-" + openId);
    }

}
