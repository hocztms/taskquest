package com.hocztms.config;

import com.hocztms.entity.CollegeEntity;
import com.hocztms.entity.TaskEntity;
import com.hocztms.service.CollegeService;
import com.hocztms.service.TaskService;
import com.hocztms.utils.RedisPageUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RedisCachePreheatBean implements InitializingBean {

    @Autowired
    private TaskService taskService;
    @Autowired
    private CollegeService collegeService;
    @Autowired
    private RedisPageUtils redisPageUtils;
    @Override
    public void afterPropertiesSet() throws Exception {
        Long i = 1L;
        List<CollegeEntity> collegeEntities = collegeService.findCollegeByPage(i);
        while (!collegeEntities.isEmpty()){
            for (CollegeEntity collegeEntity:collegeEntities){
                List<TaskEntity> taskHotPointList = taskService.findTaskHotPointList(collegeEntity.getId());
                redisPageUtils.preHeatCollegeTask(taskHotPointList,collegeEntity.getId());
            }

            collegeEntities = collegeService.findCollegeByPage(i++);
        }
    }
}
