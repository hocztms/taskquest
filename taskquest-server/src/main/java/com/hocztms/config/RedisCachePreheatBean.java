package com.hocztms.config;

import com.hocztms.dto.TaskDto;
import com.hocztms.entity.CollegeEntity;
import com.hocztms.entity.TaskEntity;
import com.hocztms.service.CollegeService;
import com.hocztms.service.TaskService;
import com.hocztms.utils.RedisPageUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class RedisCachePreheatBean implements InitializingBean {

    @Autowired
    private TaskService taskService;
    @Autowired
    private CollegeService collegeService;
    @Autowired
    private RedisPageUtils redisPageUtils;
    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;
    @Override
    public void afterPropertiesSet() throws Exception {
        //清空缓存
        Set<Object> keys = redisTemplate.keys("*");
        redisTemplate.delete(keys);


        //初始化 各学院热点数据
        Long i = 1L;
        List<CollegeEntity> collegeEntities = collegeService.findCollegeByPage(i);
        while (!collegeEntities.isEmpty()){
            for (CollegeEntity collegeEntity:collegeEntities){
                List<TaskDto> taskHotPointList = taskService.findTaskHotPointList(collegeEntity.getId());
                redisPageUtils.preHeatCollegeTask(taskHotPointList,collegeEntity.getId());
            }

            collegeEntities = collegeService.findCollegeByPage(i++);
        }
    }
}
