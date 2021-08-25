package com.hocztms.config;

import com.hocztms.entity.TaskEntity;
import com.hocztms.entity.TaskRecords;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.SimpleCacheResolver;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@Slf4j
public class TaskRecordsCacheNames extends SimpleCacheResolver implements CacheResolver {

    public static final String CollegeTaskGroup = "taskrecords";


    public TaskRecordsCacheNames(CacheManager cacheManager) {
        super(cacheManager);
    }



    @Override
    protected Collection<String> getCacheNames(CacheOperationInvocationContext<?> context){
        Object arg =  Arrays.stream(context.getArgs()).findFirst().get();
        Long taskId = null;
        if (arg instanceof Long){
            taskId = (Long) arg;
        }
        else if (arg instanceof TaskRecords){
            taskId = ((TaskRecords) arg).getTaskId();
        }

        return Collections.singleton(CollegeTaskGroup + "-" + taskId);
    }
}
