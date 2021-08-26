package com.hocztms.service;

import com.hocztms.commons.RestResult;
import com.hocztms.dto.TaskDto;
import com.hocztms.entity.TaskEntity;

import java.util.List;

public interface TaskService {

    RestResult getTaskInfo(Long taskId,String account);

    RestResult findTaskDtoByKeyword(String keyword);

    TaskEntity findTaskByTaskId(Long id);

    TaskEntity insertTask(TaskEntity taskEntity);

    TaskEntity updateTaskById(TaskEntity taskEntity);

    List<TaskDto> findTasksByCollegeId(Long collegeId, Integer status,Integer page, Integer size);

    List<TaskDto> findTaskHotPointList(Long collegeId);

}
