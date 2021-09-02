package com.hocztms.utils;

import com.hocztms.dto.TaskDto;
import com.hocztms.entity.TaskEntity;
import com.hocztms.entity.TaskRecords;
import com.hocztms.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class RedisPageUtils {

    public static final String COLLEGE_TASK_Id_PREFIX = "collegeTask-";
    public static final String TASK_RECORDS_PREFIX = "taskRecords-";
    public static final int PAGE_SIZE = 5;
    public static final int MAX_PAGE = 10;


    @Autowired
    private TaskService taskService;
    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;
    public void addCollegeTaskByCollegeId(TaskEntity taskEntity){
        if (getCollegeTaskSizeByCollegeId(taskEntity.getCollegeId())!=PAGE_SIZE*MAX_PAGE){
            redisTemplate.opsForZSet().add(COLLEGE_TASK_Id_PREFIX +taskEntity.getCollegeId(),taskEntity.getTaskId(),taskEntity.getPoints());
            return;
        }

        Set range = redisTemplate.opsForZSet().reverseRangeWithScores(COLLEGE_TASK_Id_PREFIX + taskEntity.getCollegeId(), 0, 0);
        List<Long> dtoList = redisUtils.parseToLongList(range);

        if (!dtoList.isEmpty()){
            if (redisTemplate.opsForZSet().score(COLLEGE_TASK_Id_PREFIX + taskEntity.getCollegeId(),dtoList.get(0))>taskEntity.getPoints()){
                return;
            }
            redisTemplate.opsForZSet().remove(COLLEGE_TASK_Id_PREFIX + taskEntity.getCollegeId(),dtoList.get(0));
        }
        redisTemplate.opsForZSet().add(COLLEGE_TASK_Id_PREFIX +taskEntity.getCollegeId(),taskEntity.getTaskId(),taskEntity.getPoints());
    }

    public List<TaskDto> getCollegeTaskByCollegeIdPage(Integer page, Long collegeId){
        Set objects = redisTemplate.opsForZSet().reverseRange(COLLEGE_TASK_Id_PREFIX + collegeId, page * PAGE_SIZE - PAGE_SIZE, page * PAGE_SIZE);
        List<Long> longList = redisUtils.parseToLongList(objects);
        List<TaskDto> dtos = new ArrayList<>();
        for (Long id:longList){
            TaskEntity task = taskService.findTaskByTaskId(id);
            dtos.add(CommonUtils.TaskEntityParseTaskDto(task));
        }
        return dtos;
    }

    public Long getCollegeTaskSizeByCollegeId(Long collegeId){
        return redisTemplate.opsForZSet().zCard(COLLEGE_TASK_Id_PREFIX + collegeId);
    }


    public void deleteCollegeTaskByTaskId(TaskEntity taskEntity){
        if (redisTemplate.opsForZSet().score(COLLEGE_TASK_Id_PREFIX + taskEntity.getCollegeId(), taskEntity.getTaskId())==null){
            return;
        }
        redisTemplate.opsForZSet().remove(COLLEGE_TASK_Id_PREFIX +taskEntity.getCollegeId(),taskEntity.getTaskId());

        //添加
        int page = (int) (getCollegeTaskSizeByCollegeId(taskEntity.getCollegeId())/PAGE_SIZE +1);

        List<TaskDto> dtos= taskService.findCollegeTasksPageByCollegeId(taskEntity.getCollegeId(),page);
        for (TaskDto dto:dtos){
            if (getCollegeTaskSizeByCollegeId(dto.getCollegeId())==PAGE_SIZE*MAX_PAGE){
                break;
            }

            if (redisTemplate.opsForZSet().score(COLLEGE_TASK_Id_PREFIX + dto.getCollegeId(), dto.getTaskId())!=null){
                continue;
            }


            redisTemplate.opsForZSet().add(COLLEGE_TASK_Id_PREFIX + dto.getCollegeId(),dto.getTaskId(),dto.getPoints());
        }
    }
    public void preHeatCollegeTask(List<TaskDto> taskEntities,Long collegeId){

        for (TaskDto taskDto:taskEntities){
            taskService.findTaskByTaskId(taskDto.getTaskId());
            redisTemplate.opsForZSet().add(COLLEGE_TASK_Id_PREFIX + collegeId,taskDto.getTaskId(),taskDto.getPoints());
        }
    }

    public void cleanUpCollegeTask(){
        Set<Object> keys = redisTemplate.keys(COLLEGE_TASK_Id_PREFIX + "*");
        redisTemplate.delete(keys);
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

    public List<TaskDto> getTaskCollegeOrderByPoint(Integer page,Long collegeId){
        //数据量较小
        Set objects = redisTemplate.opsForZSet().range(COLLEGE_TASK_Id_PREFIX + collegeId,0,-1);
        List<TaskDto> list = new ArrayList<>(objects);
        list.sort(new Comparator<TaskDto>() {
            @Override
            public int compare(TaskDto o1, TaskDto o2) {
                return (int) (o1.getPoints()-o2.getPoints());
            }
        });

        return list.subList(page * PAGE_SIZE - PAGE_SIZE-1, page * PAGE_SIZE-1);
    }

}
