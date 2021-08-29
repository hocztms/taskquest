package com.hocztms.service.impl;


import com.hocztms.commons.Email;
import com.hocztms.commons.RestResult;
import com.hocztms.entity.*;
import com.hocztms.service.*;
import com.hocztms.utils.MessageUtils;
import com.hocztms.utils.ResultUtils;
import com.hocztms.vo.ClassVo;
import com.hocztms.vo.NotifyVo;
import com.hocztms.vo.TaskVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class CollegeAdminServiceImpl implements CollegeAdminService {
    @Autowired
    private ClassService classService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskRecordsService recordsService;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthMessageService authMessageService;
    @Autowired
    private RabbitmqService rabbitmqService;


    @Override
    public RestResult collegeAdminCreateClass(ClassVo classVo, String username) {
        try {
            AdminEntity adminEntity = adminService.findAdminByUsername(username);
            classService.insertClass(new ClassEntity(0,adminEntity.getCollegeId(),classVo.getClassName()));
            return ResultUtils.success();
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtils.systemError();
        }
    }

    @Override
    public RestResult collegeAdminUpdateClass(ClassVo classVo, String username) {
        try {
            AdminEntity adminEntity = adminService.findAdminByUsername(username);

            ClassEntity classEntity = classService.findClassEntityById(classVo.getClassId());
            if (classEntity.getCollegeId()!=adminEntity.getCollegeId()){
                return ResultUtils.error("无权限");
            }

            classEntity.setClassName(classVo.getClassName());
            classService.updateClass(classEntity);

            return ResultUtils.success();
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtils.systemError();
        }
    }

    @Override
    public RestResult collegeAdminDeleteClassByIds(List<Long> ids, String username) {
        try {
            AdminEntity adminEntity = adminService.findAdminByUsername(username);
            for(Long id:ids){
                ClassEntity classEntityById = classService.findClassEntityById(id);

                if (classEntityById==null){
                    continue;
                }


                if (classEntityById.getCollegeId()!=adminEntity.getCollegeId()){
                    throw new RuntimeException("非法操作");
                }

                classService.deleteClass(classEntityById);
            }

            return ResultUtils.success();
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtils.systemError();
        }
    }

    @Override
    public RestResult collegeAdminCreateTask(TaskVo taskVo,String username) {
        try {
            AdminEntity adminByUsername = adminService.findAdminByUsername(username);
            TaskEntity taskEntity = new TaskEntity(0,username,adminByUsername.getCollegeId(),taskVo.getTaskName(),taskVo.getTaskContent(),taskVo.getPhone(),taskVo.getGroup(),taskVo.getDeadline(),taskVo.getType(),0,taskVo.getLimit(),taskVo.getPoints(),0,0);
            taskService.insertTask(taskEntity);
            return ResultUtils.success();
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtils.systemError();
        }
    }

    @Override
    public RestResult collegeAdminUpdateTask(TaskVo taskVo, String username) {
        try {
            TaskEntity taskEntity = taskService.findTaskByTaskId(taskVo.getTaskId());
            if (!taskEntity.getPublisher().equals(username)){
                return ResultUtils.error("无权限");
            }

            if (taskEntity.getStatus()==1){
                return ResultUtils.error("当前状态不允许更改");
            }
            if (taskEntity.getNumber()>taskVo.getLimit()&&taskVo.getLimit()!=-1){
                return ResultUtils.error("当前人数大于改变人数");
            }

            taskEntity.setTaskContent(taskVo.getTaskContent());
            taskEntity.setPhone(taskVo.getPhone());
            taskEntity.setQQGroup(taskVo.getGroup());
            taskEntity.setDeadline(taskVo.getDeadline());
            taskEntity.setType(taskVo.getType());
            taskEntity.setNumberLimit(taskVo.getLimit());
            taskEntity.setPoints(taskVo.getPoints());
            taskService.updateTaskById(taskEntity);



            return ResultUtils.success();
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtils.systemError();
        }
    }

    @Override
    public RestResult collegeAdminGetTaskApply(Long taskId, Long page, String username) {
        try {

            TaskEntity taskEntity = taskService.findTaskByTaskId(taskId);
            if (taskEntity==null){
                return ResultUtils.error("不存在");
            }
            if (!taskEntity.getPublisher().equals(username)){
                return ResultUtils.error("无权限");
            }

            List<TaskRecords> records = recordsService.findTaskApplyPage(taskId, page);
            return ResultUtils.success(records);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtils.systemError();
        }
    }

    @Override
    public RestResult collegeAdminGetTaskMember(Long taskId, Long page, String username) {
        try {

            TaskEntity taskEntity = taskService.findTaskByTaskId(taskId);
            if (taskEntity==null){
                return ResultUtils.error("任务不存在");
            }
            if (!taskEntity.getPublisher().equals(username)){
                return ResultUtils.error("无权限");
            }

            List<TaskRecords> records = recordsService.findTaskMemberPage(taskId, page);
            return ResultUtils.success(records);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtils.systemError();
        }
    }

    @Override
    public RestResult collegeAdminUnPassTaskApply(List<Long> recordsIds,String username) {
        try {
            for (Long recordsId:recordsIds){
                TaskRecords records = recordsService.findTaskRecordsById(recordsId);
                if (records==null){
                    continue;
                }

                if (records.getStatus()==1){
                    return ResultUtils.error("非法操作");
                }
                TaskEntity taskEntity = taskService.findTaskByTaskId(records.getTaskId());

                if (!taskEntity.getPublisher().equals(username)||taskEntity.getStatus()!=0){
                    throw new RuntimeException("非法操作");
                }

                recordsService.deleteTaskRecordsById(records);
                authMessageService.sendUserMessage(records.getOpenId(), MessageUtils.generateNoticeMessage(records.getOpenId(),"您申请的任务" +taskEntity.getTaskName() + "不通过"));

            }

            return ResultUtils.success();
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtils.systemError();
        }
    }

    @Override
    public RestResult collegeAdminPassTaskApply(List<Long> recordsIds, String username) {
        try {
            for (Long recordsId:recordsIds){
                TaskRecords taskRecords = recordsService.findTaskRecordsById(recordsId);
                if (taskRecords==null||taskRecords.getStatus()==1){
                    continue;
                }
                TaskEntity taskEntity = taskService.findTaskByTaskId(taskRecords.getTaskId());
                if (!taskEntity.getPublisher().equals(username)||taskEntity.getStatus()!=0){
                    throw new RuntimeException("非法操作");
                }

                if (taskEntity.getNumber()==taskEntity.getNumberLimit()&&taskEntity.getNumberLimit()!=-1){
                    return ResultUtils.error("已达人数上限");
                }

                taskEntity.setNumber(taskEntity.getNumber() + 1);
                taskService.updateTaskById(taskEntity);

                taskRecords.setStatus(1);
                recordsService.updateTaskRecords(taskRecords);

                authMessageService.sendUserMessage(taskRecords.getOpenId(), MessageUtils.generateNoticeMessage(taskRecords.getOpenId(),"您申请的任务" +taskEntity.getTaskName() + "已通过"));
            }

            return ResultUtils.success();
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtils.systemError();
        }
    }

    @Override
    public RestResult collegeAdminCompleteTask(Long taskId, String username) {
        try {
            TaskEntity taskEntity = taskService.findTaskByTaskId(taskId);
            if (!taskEntity.getPublisher().equals(username)){
                return ResultUtils.error("无权限");
            }
            if (taskEntity.getStatus()==1){
                return ResultUtils.error("请勿重复操作");
            }


            //实现 socket实现进度条
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    HashMap<String,Integer> progress = new HashMap<>();
                    progress.put("taskId", (int) taskEntity.getTaskId());
                    try {
                        List<TaskRecords> taskRecords = recordsService.findTaskMemberList(taskId);

                        double size = taskRecords.size();
                        for (double i =0 ;i<size;i++){
                            TaskRecords records = taskRecords.get((int) i);


                            UserEntity userEntity = userService.findUserByOpenId(records.getOpenId());
                            userEntity.setPoint(userEntity.getPoint() + taskEntity.getPoints());

                            userService.updateUserByOpenId(userEntity);

                            progress.put("progress", (int) ((i+1)/size*100.0));

//                            progress.put("progress", (int) i);
//                            Thread.sleep(500);
                            //为了让进度条体现更可观
                            Thread.sleep(1000);
                            authMessageService.sendCollegeAdminTaskProgress(username,progress);
                        }

                        taskEntity.setStatus(0);
                        taskService.updateTaskById(taskEntity);

                    }catch (Exception e){
                        e.printStackTrace();
                        progress.put("progress", (int) -1);
                        authMessageService.sendCollegeAdminTaskProgress(username,progress);
                    }
                }
            });
            thread.start();

            return ResultUtils.success("任务完成中 请等待");
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtils.systemError();
        }
    }

    @Override
    public RestResult collegeAdminRejectTaskMember(Long recordsId, String username) {
        try {
            TaskRecords records = recordsService.findTaskRecordsById(recordsId);
            TaskEntity taskEntity = taskService.findTaskByTaskId(records.getTaskId());

            if (!taskEntity.getPublisher().equals(username)){
                return ResultUtils.error("无权限");
            }

            if (taskEntity.getStatus()!=0) {
                return ResultUtils.error("当前状态不允许更改");
            }

            recordsService.deleteTaskRecordsById(records);
            authMessageService.sendUserMessage(records.getOpenId(),MessageUtils.generateNoticeMessage(records.getOpenId(),"您的任务"+ taskEntity.getTaskName() +"未完成，，已被踢出" ));
            return ResultUtils.success();
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtils.systemError();
        }
    }

    @Override
    public RestResult collegeAdminNotifyTaskMember(NotifyVo notifyVo, String username) {
        try {
            TaskEntity taskEntity = taskService.findTaskByTaskId(notifyVo.getTaskId());

            if (!taskEntity.getPublisher().equals(username)){
                return ResultUtils.error("无权限");
            }


            List<TaskRecords> taskRecords = recordsService.findTaskMemberList(notifyVo.getTaskId());

            //开启异步
            Thread emailThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    List<Email> emails = new ArrayList<>();
                    for (TaskRecords records:taskRecords){
                        emails.add(new Email(records.getEmail(),"通知","您的任务" + taskEntity.getTaskName() + "有新通知:" + notifyVo.getContent(),new Date(),"null"));
                    }
                    rabbitmqService.sendEmailList(emails);
                }
            });

            Thread messageThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (TaskRecords records:taskRecords){
                        authMessageService.sendUserMessage(records.getOpenId(),MessageUtils.generateNoticeMessage(records.getOpenId(),notifyVo.getContent(),"任务:" +taskEntity.getTaskName() ));
                    }
                }
            });
            emailThread.start();
            messageThread.start();


            return ResultUtils.success();
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtils.systemError();
        }
    }


}
