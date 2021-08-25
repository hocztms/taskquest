package com.hocztms.service;

import com.hocztms.entity.TaskRecords;

import java.util.List;

public interface TaskRecordsService {

    TaskRecords insertTaskRecords(TaskRecords taskRecords);

    TaskRecords updateTaskRecords(TaskRecords taskRecords);

    void userDeleteTaskRecords(TaskRecords taskRecords);

    void deleteTaskRecordsById(TaskRecords taskRecords);

    TaskRecords findTaskRecordsById(Long Id);

    TaskRecords findUserTaskRecords(Long taskId, Long studentId);

    List<TaskRecords> findTaskMemberPage(Long taskId, Long page);

    List<TaskRecords> findUserTaskRecords(String openId, Long page);

    List<TaskRecords> findTaskApplyPage(Long taskId, Long page);

    List<TaskRecords> findTaskMemberList(Long taskId);

    List<TaskRecords> findTaskRecordsList(Long taskId);
}
