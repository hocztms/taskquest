package com.hocztms.service.impl;

import com.hocztms.commons.RestResult;
import com.hocztms.dto.TaskDto;
import com.hocztms.dto.UserDto;
import com.hocztms.entity.TaskEntity;
import com.hocztms.entity.TaskRecords;
import com.hocztms.service.*;
import com.hocztms.utils.RedisPageUtils;
import com.hocztms.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private RedisPageUtils redisPageUtils;
    @Autowired
    private UserService userService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskRecordsService recordsService;
    @Autowired
    private AuthMessageService authMessageService;
    @Override
    public RestResult studentGetCollegeTask(String openId, Integer page) {
        try {
            UserDto user = userService.findUserDtoByOpenId(openId);

            //大于 缓存最大容量
            if (page>RedisPageUtils.MAX_PAGE){
                List<TaskDto> tasksByCollegeId = taskService.findTasksByCollegeId(user.getCollegeId(), 0,page, 5);

                return ResultUtils.success(tasksByCollegeId);
            }
            List<TaskDto> collegeTaskByCollegeIdPage = redisPageUtils.getCollegeTaskByCollegeIdPage(page, user.getCollegeId());



            return ResultUtils.success(collegeTaskByCollegeIdPage);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtils.systemError();
        }
    }

    @Override
    public RestResult studentGetCollegeTaskOrderByPoints(String openId, Integer page) {
        try {
            UserDto userDto = userService.findUserDtoByOpenId(openId);


            return ResultUtils.success(redisPageUtils.getTaskCollegeOrderByPoint(page,userDto.getCollegeId()));

        }catch (Exception e){
            e.printStackTrace();
            return ResultUtils.systemError();
        }
    }

    @Override
    public RestResult studentApplyCollegeTask(Long taskId, String openId) {
        try {
            UserDto userDto = userService.findUserDtoByOpenId(openId);

            if (userDto.getEmail()==null){
                return ResultUtils.error("请先绑定邮箱");
            }

            TaskEntity taskEntity = taskService.findTaskByTaskId(taskId);
            if (taskEntity.getCollegeId()!=userDto.getCollegeId()){
                return ResultUtils.error("无权限");
            }

            if (taskEntity.getType()!=0){
                return ResultUtils.error("错误方法");
            }
            TaskRecords records = recordsService.findUserTaskRecords(taskEntity.getTaskId(),userDto.getStudentId());
            if (records!=null){
                return ResultUtils.error("请勿充重复操作");
            }

            if (taskEntity.getNumber()==taskEntity.getNumberLimit()&&taskEntity.getNumberLimit()!=-1){
                redisPageUtils.deleteCollegeTaskByTaskId(taskEntity);
                return ResultUtils.error("已达人数上线");
            }

            if (new Date().after(taskEntity.getDeadline())||taskEntity.getStatus()!=0){
                redisPageUtils.deleteCollegeTaskByTaskId(taskEntity);
                return ResultUtils.error("任务已经截至");
            }
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    TaskRecords taskRecords = new TaskRecords(0,taskId,openId,userDto.getStudentId(),userDto.getStudentName(),userDto.getEmail(),0,taskEntity.getType());
                    recordsService.insertTaskRecords(taskRecords);
                    authMessageService.sendCollegeAdminMessage(taskEntity.getPublisher(),"您的任务" + taskEntity.getTaskName() + "有新的审核...");

                }
            });
            thread.start();


            return ResultUtils.success();
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtils.systemError();
        }
    }

    @Override
    public RestResult studentGrabCollegeTask(Long taskId, String openId) {
        try {
            UserDto userDto = userService.findUserDtoByOpenId(openId);

            if (userDto.getEmail()==null){
                return ResultUtils.error("请先绑定邮箱");
            }

            TaskEntity taskEntity = taskService.findTaskByTaskId(taskId);
            if (taskEntity.getCollegeId()!=userDto.getCollegeId()){
                return ResultUtils.error("无权限");
            }

            if (taskEntity.getType()!=1){
                return ResultUtils.error("错误方法");
            }
            TaskRecords records = recordsService.findUserTaskRecords(taskEntity.getTaskId(),userDto.getStudentId());
            if (records!=null){
                return ResultUtils.error("请勿充重复操作");
            }

            if (taskEntity.getNumber()==taskEntity.getNumberLimit()&&taskEntity.getNumberLimit()!=-1){
                redisPageUtils.deleteCollegeTaskByTaskId(taskEntity);
                return ResultUtils.error("已达人数上线");
            }

            if (new Date().after(taskEntity.getDeadline())||taskEntity.getStatus()!=0) {
                redisPageUtils.deleteCollegeTaskByTaskId(taskEntity);
                return ResultUtils.error("任务已经截至");
            }



            taskEntity.setNumber(taskEntity.getNumber() + 1);
            taskService.updateTaskById(taskEntity);


            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    TaskRecords taskRecords = new TaskRecords(0, taskId, openId, userDto.getStudentId(), userDto.getStudentName(), userDto.getEmail(), 1, taskEntity.getType());
                    recordsService.insertTaskRecords(taskRecords);
                    authMessageService.sendCollegeAdminMessage(taskEntity.getPublisher(),"您的任务" + taskEntity.getTaskName() + "有新的抢单成功成员...");

                }
            });
            thread.start();


            return  ResultUtils.success();
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtils.systemError();
        }
    }

    @Override
    public RestResult studentGetTaskRecords(String openId, Long page) {
        try {
            List<TaskRecords> taskRecords = recordsService.findUserTaskRecords(openId, page);
            return ResultUtils.success(taskRecords);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtils.systemError();
        }
    }

    @Override
    public RestResult studentDeleteTaskRecords(List<Long> ids, String openId) {
        try {
            for (Long id:ids){
                TaskRecords records = recordsService.findTaskRecordsById(id);

                if (!records.getOpenId().equals(openId)){
                    throw new RuntimeException("非法操作");
                }

                if (records==null||records.getDeleted()==1){
                    continue;
                }

                recordsService.userDeleteTaskRecords(records);
            }

            return ResultUtils.success();
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtils.systemError();
        }
    }
}
