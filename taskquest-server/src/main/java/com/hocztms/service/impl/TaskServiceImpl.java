package com.hocztms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hocztms.commons.Email;
import com.hocztms.commons.RestResult;
import com.hocztms.config.cacheResolvers.CollegeTaskPageCacheResolver;
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
import java.util.Date;
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
        if (!redisTemplate.hasKey("task::" + id)) {
            synchronized (this) {
                TaskEntity value = (TaskEntity) redisTemplate.opsForValue().get("task::" + id);

                if (!redisTemplate.hasKey("task::" + id)) {
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

        //自动刷新
        if (redisTemplate.getExpire("task::"+ id)<20){
            redisTemplate.expire("task::" + id,30 + new Random().nextInt(20),TimeUnit.MINUTES);
        }
        TaskEntity taskEntity = (TaskEntity) redisTemplate.opsForValue().get("task::" + id);

        if (taskEntity.getNumber()==taskEntity.getNumberLimit() ||taskEntity.getDeadline().before(new Date()) ){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    redisPageUtils.deleteCollegeTaskByTaskId(taskEntity);
                }
            });
            thread.start();
        }


        return taskEntity;
    }

    @Override
    public TaskDto addRedisTaskDtoByDelete(Long taskId,Long collegeId) {
        return taskMapper.selectRedisTaskDto(taskId,collegeId,new Date());
    }

    @Override
    @CacheEvict(allEntries = true,cacheResolver = "CollegeTaskPageCacheResolver")
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
    @Cacheable(cacheResolver = "CollegeTaskPageCacheResolver",key = "#page")
    public List<TaskDto> findTasksByCollegeId(Long collegeId,Integer status,Integer page, Integer size) {
        log.info("学院：{} 任务清单走数据库...",collegeId);
        return taskMapper.selectTaskDtoListByCollegeId(collegeId,status,new Page(page,size));
    }

    @Override
    public List<TaskDto> findTaskHotPointList(Long collegeId) {
        return taskMapper.selectTaskHotPointList(collegeId,new Date(),new Page<>(1,RedisPageUtils.PAGE_SIZE*RedisPageUtils.MAX_PAGE));
    }


    @Bean("CollegeTaskPageCacheResolver")
    public CollegeTaskPageCacheResolver cacheResolver() {
        return new CollegeTaskPageCacheResolver(cacheManager);
    }
}
