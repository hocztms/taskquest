package com.hocztms.config.cacheResolvers;

import com.hocztms.entity.ClassEntity;
import com.hocztms.entity.TaskEntity;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.SimpleCacheResolver;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class ClassPageCacheResolver extends SimpleCacheResolver implements CacheResolver {
    public static final String CollegeTaskGroup = "collegeClass";


    public ClassPageCacheResolver(CacheManager cacheManager) {
        super(cacheManager);
    }



    @Override
    protected Collection<String> getCacheNames(CacheOperationInvocationContext<?> context){
        Object arg =  Arrays.stream(context.getArgs()).findFirst().get();
        Long collegeId = null;
        if (arg instanceof Long){
            collegeId = (Long) arg;
        }
        else if (arg instanceof ClassEntity){
            collegeId = ((ClassEntity) arg).getCollegeId();
        }

        return Collections.singleton(CollegeTaskGroup+"-"+collegeId);

    }
}
