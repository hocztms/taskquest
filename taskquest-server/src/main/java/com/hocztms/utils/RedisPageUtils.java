package com.hocztms.utils;

import com.hocztms.dto.TaskDto;
import com.hocztms.entity.TaskRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisPageUtils {

    public static final String COLLEGE_TASK_PREFIX = "collegeTask-";
    public static final String TASK_RECORDS_PREFIX = "taskRecords-";
    public static final int PAGE_SIZE = 5;
    public static final int MAX_PAGE = 10;

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;
    public void addCollegeTaskByCollegeId(TaskDto taskDto){
        if (redisTemplate.opsForZSet().zCard(COLLEGE_TASK_PREFIX +taskDto.getCollegeId())==PAGE_SIZE* MAX_PAGE){
            redisTemplate.opsForZSet().removeRange(COLLEGE_TASK_PREFIX +taskDto.getCollegeId(),0,0);
        }
        redisTemplate.opsForZSet().add(COLLEGE_TASK_PREFIX +taskDto.getCollegeId(),taskDto,taskDto.getTaskId());
    }

    public List<TaskDto> getCollegeTaskByCollegeIdPage(Integer page, Long collegeId){
        Set objects = redisTemplate.opsForZSet().reverseRange(COLLEGE_TASK_PREFIX + collegeId, page * PAGE_SIZE - PAGE_SIZE, page * PAGE_SIZE);
        List<TaskDto> list = new ArrayList<>(objects);
        return list;
    }

    public void updateCollegeTaskByTaskId(TaskDto taskDto){
        synchronized (this){
            TaskDto task = findCollegeTaskByScore(taskDto.getCollegeId(),taskDto.getTaskId());
            if (task==null){
                return;
            }
            redisTemplate.opsForZSet().remove(COLLEGE_TASK_PREFIX+taskDto.getCollegeId(),task);
            redisTemplate.opsForZSet().add(COLLEGE_TASK_PREFIX + taskDto.getCollegeId(),taskDto,task.getTaskId());
        }
    }

    public void deleteCollegeTaskByTaskId(Long collegeId,Long score){
            TaskDto task = findCollegeTaskByScore(collegeId,score);
            redisTemplate.opsForZSet().remove(COLLEGE_TASK_PREFIX+task.getCollegeId(),task);
    }

    public TaskDto findCollegeTaskByScore(Long collegeId,Long score){
        Set objects = redisTemplate.opsForZSet().rangeByScore(COLLEGE_TASK_PREFIX + collegeId, score, score);
        List<TaskDto> list = new ArrayList<>(objects);
        if (list.isEmpty()){
            return null;
        }
        return list.get(0);
    }

    public void addTaskRecordsList(List<TaskRecords> taskRecords,Long taskId){
        if (taskRecords.isEmpty()){
            //防缓存穿透
            redisTemplate.opsForZSet().add(TASK_RECORDS_PREFIX + taskId,null,2);
        }

        for (TaskRecords records:taskRecords){
            redisTemplate.opsForZSet().add(TASK_RECORDS_PREFIX + records.getTaskId(),records,records.getStatus());
        }
        redisTemplate.expire(TASK_RECORDS_PREFIX + taskId,30 + new Random().nextInt(20), TimeUnit.MINUTES);
    }

    public void addTaskRecords(TaskRecords records){
        if (taskRecordsIsEmpty(records.getTaskId())){
            return;
        }

        redisTemplate.opsForZSet().add(TASK_RECORDS_PREFIX + records.getTaskId(),records,records.getStatus());
    }

    public void updateTaskRecords(TaskRecords records){
        if (taskRecordsIsEmpty(records.getTaskId())){
            return;
        }

        //只用于更新
        redisTemplate.opsForZSet().add(TASK_RECORDS_PREFIX + records.getTaskId(),records,records.getStatus());
        records.setStatus(0);
        redisTemplate.opsForZSet().remove(TASK_RECORDS_PREFIX + records.getTaskId(),records);
    }

    public void deleteTaskRecords(TaskRecords records){
        if (taskRecordsIsEmpty(records.getTaskId())){
            return;
        }

        redisTemplate.opsForZSet().remove(TASK_RECORDS_PREFIX + records.getTaskId(),records);
    }


    public List<TaskRecords> findTaskRecordsByPage(Long taskId, Long page, Integer status){
        Set objects = redisTemplate.opsForZSet().rangeByScore(TASK_RECORDS_PREFIX + taskId, status, status, page * PAGE_SIZE - PAGE_SIZE, page * PAGE_SIZE);

        List<TaskRecords> records = new ArrayList<>(objects);
        return records;
    }

    public boolean taskRecordsIsEmpty(Long taskId) {
        if (redisTemplate.opsForZSet().zCard(TASK_RECORDS_PREFIX + taskId) == 0) {
            return true;
        }
        return false;
    }


    public List<TaskRecords> findTaskRecordsList(Long taskId,Integer status){
        Set objects = redisTemplate.opsForZSet().rangeByScore(TASK_RECORDS_PREFIX + taskId, status, status);

        List<TaskRecords> list = new ArrayList<>(objects);
        return list;
    }
}
