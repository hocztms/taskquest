package com.hocztms.config.cacheResolvers;

import com.hocztms.entity.TaskEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.SimpleCacheResolver;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Slf4j
public class CollegeTaskPageCacheResolver extends SimpleCacheResolver implements CacheResolver {

    public static final String CollegeTaskGroup = "collegeTask";


    public CollegeTaskPageCacheResolver(CacheManager cacheManager) {
        super(cacheManager);
    }



    @Override
    protected Collection<String> getCacheNames(CacheOperationInvocationContext<?> context){
        Object arg =  Arrays.stream(context.getArgs()).findFirst().get();
        Long collegeId = null;
        if (arg instanceof Long){
            collegeId = (Long) arg;
        }
        else if (arg instanceof TaskEntity){
            collegeId = ((TaskEntity) arg).getCollegeId();
        }

        return Collections.singleton(CollegeTaskGroup+"-"+collegeId);

    }

}
