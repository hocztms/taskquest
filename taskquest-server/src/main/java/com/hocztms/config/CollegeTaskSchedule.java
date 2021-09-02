package com.hocztms.config;

import com.hocztms.dto.TaskDto;
import com.hocztms.entity.CollegeEntity;
import com.hocztms.service.CollegeService;
import com.hocztms.service.TaskService;
import com.hocztms.utils.RedisIpUtils;
import com.hocztms.utils.RedisPageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.parameters.P;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;


@Configuration
@EnableScheduling
@Slf4j
@EnableAsync
public class CollegeTaskSchedule {

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;
    @Autowired
    private RedisIpUtils redisIpUtils;
    @Autowired
    private CollegeService collegeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RedisPageUtils redisPageUtils;

    @Async
    @Scheduled(fixedRate=1000*60 * 15)
    public void configureTasks() {

        if (!redisIpUtils.ifCanRefreshCollegeTask()){
            log.info("当前服务繁忙拒绝更新....当前时间为:{}",new Date());
            return;
        }
        log.info("准备开始执行学院任务刷新。。。。当前时间为:{}",new Date());


        redisIpUtils.lockUri("/user/getCollegeId");

        redisPageUtils.cleanUpCollegeTask();

        List<CollegeEntity> collegeEntities = collegeService.findCollegeList();

        //保证线程全部执行完解锁
        CountDownLatch countDownLatch = new CountDownLatch(collegeEntities.size());

        for (CollegeEntity collegeEntity:collegeEntities){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<TaskDto> taskHotPointList = taskService.findTaskHotPointList(collegeEntity.getId());
                        redisPageUtils.preHeatCollegeTask(taskHotPointList,collegeEntity.getId());
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        log.info("学院: {} 任务刷新完毕",collegeEntity.getCollegeName());
                        countDownLatch.countDown();
                    }
                }
            });
            thread.start();
        }
        redisIpUtils.unLockUri("/user/getCollegeId");

        log.info("学院任务刷新结束....当前时间为:{}",new Date());
    }
}
