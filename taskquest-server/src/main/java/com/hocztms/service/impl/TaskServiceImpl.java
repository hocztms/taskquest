package com.hocztms.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hocztms.commons.Email;
import com.hocztms.commons.RestResult;
import com.hocztms.config.CollegeTaskPageCacheNames;
import com.hocztms.dto.TaskDto;
import com.hocztms.dto.UserDto;
import com.hocztms.entity.TaskEntity;
import com.hocztms.entity.TaskRecords;
import com.hocztms.mapper.TaskMapper;
import com.hocztms.security.entity.MyUserDetails;
import com.hocztms.service.AdminService;
import com.hocztms.service.TaskRecordsService;
import com.hocztms.service.TaskService;
import com.hocztms.service.UserService;
import com.hocztms.utils.RedisPageUtils;
import com.hocztms.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private RedisPageUtils redisPageUtils;
    @Autowired
    private TaskRecordsService recordsService;
    @Autowired
    private UserService userService;
    @Autowired
    private AdminService adminService;

    @Override
    public RestResult getTaskInfo(Long taskId, String account) {
        try {
            TaskEntity taskByTaskId = findTaskByTaskId(taskId);

            if (taskByTaskId.getPublisher().equals(account)){
                return ResultUtils.success(taskByTaskId);
            }

            UserDto userDto = userService.findUserDtoByOpenId(account);
            if (userDto==null){
                return ResultUtils.error("无权限");
            }
            List<Email> emails = new ArrayList<>();

            TaskRecords taskRecords = recordsService.findUserTaskRecords(taskId,userDto.getStudentId());
            if (taskRecords==null||taskRecords.getStatus()==0){
                return ResultUtils.error("无权限");
            }
            return ResultUtils.success(taskByTaskId);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtils.systemError();
        }
    }

    @Override
    public RestResult findTaskDtoByKeyword(String keyword) {
        try {
            Long collegeId;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
            if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("student"))){
                System.out.println("student");
                collegeId = userService.findUserDtoByOpenId(userDetails.getUsername()).getCollegeId();
            }
            else {
                collegeId = adminService.findAdminByUsername(userDetails.getUsername()).getCollegeId();
            }

            return ResultUtils.success(taskMapper.selectTaskDtoByKeyWord(keyword,collegeId));
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtils.systemError();
        }
    }

    @Override
    public TaskEntity findTaskByTaskId(Long id) {
        TaskEntity task = (TaskEntity) redisTemplate.opsForValue().get("task::" + id);

        if (task == null) {
            synchronized (this) {
                TaskEntity value = (TaskEntity) redisTemplate.opsForValue().get("task::" + id);

                if (value != null) {
                    return value;
                } else {
                    log.info("taskId:{} 走数据库。。。。",id);
                    TaskEntity taskEntity = taskMapper.selectById(id);
                    if (taskEntity == null) {
                        redisTemplate.opsForValue().set("task::" + id, null, 5, TimeUnit.MINUTES);
                    } else {
                        Random random = new Random();
                        redisTemplate.opsForValue().set("task::" + id, taskEntity, 30 + random.nextInt(20), TimeUnit.MINUTES);
                    }
                    return taskEntity;
                }
            }
        }
        return task;
    }

    @Override
    @CacheEvict(allEntries = true,cacheResolver = "CollegeTaskPageCacheNames")
    public TaskEntity insertTask(TaskEntity taskEntity) {
        int insert = taskMapper.insert(taskEntity);
        redisPageUtils.addCollegeTaskByCollegeId(new TaskDto(taskEntity.getTaskId(),taskEntity.getCollegeId(),taskEntity.getTaskName(),taskEntity.getTaskContent(),taskEntity.getType(),taskEntity.getNumber(),taskEntity.getPoints(),taskEntity.getStatus()));

        redisTemplate.opsForValue().set("task::"+taskEntity.getTaskId(),taskEntity,30 + new Random().nextInt(20),TimeUnit.MINUTES);
        return taskEntity;
    }

    @Override
    public TaskEntity updateTaskById(TaskEntity taskEntity) {
        int  i = taskMapper.updateById(taskEntity);

        if (i==0){
            throw new RuntimeException("更新失败");
        }
        redisPageUtils.updateCollegeTaskByTaskId(new TaskDto(taskEntity.getTaskId(),taskEntity.getCollegeId(),taskEntity.getTaskName(),taskEntity.getTaskContent(),taskEntity.getType(),taskEntity.getNumber(),taskEntity.getPoints(),taskEntity.getStatus()));
        redisTemplate.opsForValue().set("task::"+taskEntity.getTaskId(),taskEntity,30 + new Random().nextInt(20),TimeUnit.MINUTES);
        return taskEntity;
    }


    @Override
    @Cacheable(cacheResolver = "CollegeTaskPageCacheNames",key = "#page")
    public List<TaskDto> findTasksByCollegeId(Long collegeId,Integer status,Integer page, Integer size) {

        return taskMapper.selectTaskDtoListByCollegeId(collegeId,status,new Page(page,size));
    }


    @Bean("CollegeTaskPageCacheNames")
    public CollegeTaskPageCacheNames cacheResolver() {
        return new CollegeTaskPageCacheNames(cacheManager);
    }
}
