package com.hocztms.utils;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.api.R;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisIpUtils {

    public static final String IP_LIMIT_PREFIX = "ipLimit-";

    public static final String URI_LIMIT_PREFIX ="uriLimit-";
    public static final Long ipLimit = 15L;
    public static final Long uriLimit = 150L;
    public static final Long busyLevel = 50L;


    @Autowired
    private RedisTemplate<String,Long> redisTemplate;

    public void incIpLimit(String ip){
        Long limit =  redisTemplate.opsForValue().get(IP_LIMIT_PREFIX + ip);
        if (limit==null){
            redisTemplate.opsForValue().set(IP_LIMIT_PREFIX + ip,1L,5, TimeUnit.SECONDS);
            return;
        }
        redisTemplate.opsForValue().increment(IP_LIMIT_PREFIX+ip);
    }


    public boolean checkIpLimit(String ip){
        Long limit =  redisTemplate.opsForValue().get(IP_LIMIT_PREFIX + ip);
        if (limit==null){
            return true;
        }
        if (limit<uriLimit){
            return true;
        }
        return false;
    }

    public boolean checkUriLimit(String uri){
        Long limit =  redisTemplate.opsForValue().get(URI_LIMIT_PREFIX + uri);
        if (limit ==null){
            return true;
        }
        if (limit<ipLimit){
            return true;
        }
        return false;
    }


    public void incUriLimit(String uri){
        Long limit =  redisTemplate.opsForValue().get(URI_LIMIT_PREFIX + uri);
        if (limit==null){
            redisTemplate.opsForValue().set(URI_LIMIT_PREFIX + uri,1L,30, TimeUnit.SECONDS);
            return;
        }
        redisTemplate.opsForValue().increment(URI_LIMIT_PREFIX+uri);
    }

    public boolean ifCanRefreshCollegeTask(){
        Long limit = redisTemplate.opsForValue().get(URI_LIMIT_PREFIX + "/user/getCollegeTask");
        if (limit==null||limit<busyLevel){
            return true;
        }
        return false;
    }

    public void banIp(String ip){
        redisTemplate.opsForValue().set(IP_LIMIT_PREFIX+ip,ipLimit,5,TimeUnit.SECONDS);
    }

    public void banUri(String uri){
        redisTemplate.opsForValue().set(URI_LIMIT_PREFIX + uri,ipLimit,10,TimeUnit.SECONDS);
    }

    public void lockUri(String uri){
        redisTemplate.opsForValue().set(URI_LIMIT_PREFIX + uri,uriLimit);
    }

    public void unLockUri(String uri){
        redisTemplate.delete(URI_LIMIT_PREFIX +uri);
    }
}
