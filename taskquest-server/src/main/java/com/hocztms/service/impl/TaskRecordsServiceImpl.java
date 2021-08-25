package com.hocztms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hocztms.config.UserTaskRecordsCacheResolver;
import com.hocztms.entity.TaskRecords;
import com.hocztms.mapper.TaskRecordsMapper;
import com.hocztms.service.TaskRecordsService;
import com.hocztms.utils.RedisPageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TaskRecordsServiceImpl implements TaskRecordsService {


    @Autowired
    private TaskRecordsMapper recordsMapper;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RedisPageUtils redisPageUtils;


    @Override
    @Caching(
            put = {
                    @CachePut(value = "taskRecords",key = "#result.id"),
    },
            evict = {
                    @CacheEvict(cacheResolver = "UserTaskRecordsCacheResolver",allEntries = true),
                    @CacheEvict(value = "taskStudentRecords",key = "#taskRecords.taskId +'-'+ #taskRecords.studentId")
            }
    )
    public TaskRecords insertTaskRecords(TaskRecords taskRecords) {
        recordsMapper.insert(taskRecords);

        redisPageUtils.addTaskRecords(taskRecords);
        return taskRecords;
    }

    @Override
    @Caching(
            put = {
                    @CachePut(value = "taskRecords",key = "#result.id")
            },
            evict = {
                    @CacheEvict(cacheResolver = "UserTaskRecordsCacheResolver",allEntries = true),
                    @CacheEvict(value = "taskStudentRecords",key = "#taskRecords.taskId +'-'+ #taskRecords.studentId"),
            }
    )
    public TaskRecords updateTaskRecords(TaskRecords taskRecords) {
        recordsMapper.updateById(taskRecords);
        redisPageUtils.updateTaskRecords(taskRecords);
        return taskRecords;
    }

    @Override
    @CacheEvict(cacheResolver = "UserTaskRecordsCacheResolver",allEntries = true,condition = "#taskRecords.deleted==1")
    public void userDeleteTaskRecords(TaskRecords taskRecords) {
        redisPageUtils.deleteTaskRecords(taskRecords);
        taskRecords.setDeleted(1);
        recordsMapper.updateById(taskRecords);
        redisPageUtils.addTaskRecords(taskRecords);
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "taskRecords",key = "#records.id"),
                    @CacheEvict(cacheResolver = "UserTaskRecordsCacheResolver",allEntries = true),
            }
    )
    public void deleteTaskRecordsById(TaskRecords records) {


        recordsMapper.deleteById(records.getId());
        redisPageUtils.deleteTaskRecords(records);
    }

    @Override
    @Cacheable(value = "taskRecords",key = "#id")
    public TaskRecords findTaskRecordsById(Long id) {
        return recordsMapper.selectById(id);
    }

    @Override
    //该状态下的taskRecords 不会再次改变 只是用来判断是否权限
    @Cacheable(value = "taskStudentRecords",key = "#taskId +'-'+ #studentId")
    public TaskRecords findUserTaskRecords(Long taskId, Long studentId) {
        QueryWrapper<TaskRecords> wrapper = new QueryWrapper<>();
        wrapper.eq("task_id",taskId);
        wrapper.eq("student_id",studentId);
        return recordsMapper.selectOne(wrapper);
    }



    @Override
    public List<TaskRecords> findTaskMemberPage(Long taskId, Long page) {
        if (redisPageUtils.taskRecordsIsEmpty(taskId)){
            synchronized (this){
                if (redisPageUtils.taskRecordsIsEmpty(taskId)){
                    List<TaskRecords> taskRecords = findTaskRecordsList(taskId);
                    redisPageUtils.addTaskRecordsList(taskRecords,taskId);
                }

                return redisPageUtils.findTaskRecordsByPage(taskId,page,1);
            }
        }
        return redisPageUtils.findTaskRecordsByPage(taskId, page, 1);
    }



    @Override
    public List<TaskRecords> findTaskApplyPage(Long taskId, Long page) {
        if (redisPageUtils.taskRecordsIsEmpty(taskId)){
            synchronized (this){
                if (redisPageUtils.taskRecordsIsEmpty(taskId)){
                    List<TaskRecords> taskRecords = findTaskRecordsList(taskId);
                    redisPageUtils.addTaskRecordsList(taskRecords,taskId);
                }

                return redisPageUtils.findTaskRecordsByPage(taskId,page,0);
            }
        }
        return redisPageUtils.findTaskRecordsByPage(taskId, page, 0);
    }

    @Override
    public List<TaskRecords> findTaskMemberList(Long taskId) {
        if (redisPageUtils.taskRecordsIsEmpty(taskId)){
            synchronized (this){
                if (redisPageUtils.taskRecordsIsEmpty(taskId)){
                    List<TaskRecords> taskRecords = findTaskRecordsList(taskId);
                    redisPageUtils.addTaskRecordsList(taskRecords,taskId);
                }

                return redisPageUtils.findTaskRecordsList(taskId,1);
            }
        }
        return redisPageUtils.findTaskRecordsList(taskId,1);
    }

    @Override
    public List<TaskRecords> findTaskRecordsList(Long taskId) {
        log.info("任务id :{} 走数据库查询纪录。。。。",taskId);
        QueryWrapper<TaskRecords>wrapper = new QueryWrapper<>();
        wrapper.eq("task_Id",taskId);
        return recordsMapper.selectList(wrapper);
    }



    @Override
    @Cacheable(cacheResolver = "UserTaskRecordsCacheResolver",key ="#page" )
    public List<TaskRecords> findUserTaskRecords(String openId, Long page) {
        QueryWrapper<TaskRecords> wrapper = new QueryWrapper<>();
        wrapper.eq("open_id",openId);
        wrapper.eq("deleted",0);
        wrapper.orderByAsc("status");
        wrapper.orderByAsc("completed");
        return recordsMapper.selectList(wrapper);
    }

    @Bean("UserTaskRecordsCacheResolver")
    public UserTaskRecordsCacheResolver userTaskRecordsCacheNames(){
        return new UserTaskRecordsCacheResolver(cacheManager);
    }
}
