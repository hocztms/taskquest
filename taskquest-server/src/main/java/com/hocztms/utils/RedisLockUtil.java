package com.hocztms.utils;

import com.hocztms.commons.RestResult;
import com.hocztms.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RedisLockUtil {


    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private StudentService studentService;


    public RestResult grabTaskLock(Long taskId, String openId){
        log.info("用户:{}  进入排队等待",openId);
        RLock rLock = redissonClient.getFairLock(taskId.toString());

        //设置基础值为非
        boolean succeed = false;

        try {

            //等待50s
            succeed = rLock.tryLock(50,20, TimeUnit.SECONDS);
        }catch (Exception e){
            e.printStackTrace();
        }

        if (succeed){
            log.info("当前用户:{}  正在抢任务:[} ",openId,taskId);
            try {
                return studentService.studentGrabCollegeTask(taskId,openId);
            }catch (Exception e){
                e.printStackTrace();
                return ResultUtils.systemError(e);
            }finally {
                rLock.unlock();
            }
        }
        return  ResultUtils.error("服务繁忙");
    }
}
