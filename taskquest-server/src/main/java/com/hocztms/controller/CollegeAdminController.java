package com.hocztms.controller;

import com.hocztms.commons.RestResult;
import com.hocztms.jwt.JwtAuthService;
import com.hocztms.service.CollegeAdminService;
import com.hocztms.vo.ClassVo;
import com.hocztms.vo.NotifyVo;
import com.hocztms.vo.TaskVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/collegeAdmin")
@PreAuthorize("hasAuthority('collegeAdmin')")
@Api(tags = "学院管理员接口，权限要求：学院管理员")
//已测试
public class CollegeAdminController {

    @Autowired
    private CollegeAdminService collegeAdminService;
    @Autowired
    private JwtAuthService jwtAuthService;

    @ApiOperation("创建班级")
    @PostMapping("/createClass")
    public RestResult createClass(@Validated@RequestBody ClassVo classVo,HttpServletRequest request){
        String account = jwtAuthService.getAccountFromToken(request);
        return collegeAdminService.collegeAdminCreateClass(classVo,account );
    }

    @ApiOperation("更新班级")
    @PostMapping("/updateClass")
    public RestResult updateClass(@Validated@RequestBody ClassVo classVo,HttpServletRequest request){
        String account = jwtAuthService.getAccountFromToken(request);
        return collegeAdminService.collegeAdminUpdateClass(classVo,account);
    }

    @ApiOperation("删除班级")
    @PostMapping("/deleteClass")
    public RestResult deleteClass(@RequestBody List<Long> ids,HttpServletRequest request){
        String account = jwtAuthService.getAccountFromToken(request);
        return collegeAdminService.collegeAdminDeleteClassByIds(ids,account);
    }


    @ApiOperation("创建任务")
    @PostMapping("/createTask")
    public RestResult createTask(@Validated@RequestBody TaskVo taskVo, HttpServletRequest request){
        String account = jwtAuthService.getAccountFromToken(request);
        return collegeAdminService.collegeAdminCreateTask(taskVo,account);
    }

    @ApiOperation("更新任务")
    @PutMapping("/updateTask")
    public RestResult updateTask(@Validated@RequestBody TaskVo taskVo, HttpServletRequest request){
        String account = jwtAuthService.getAccountFromToken(request);
        return collegeAdminService.collegeAdminUpdateTask(taskVo,account);
    }


    @ApiOperation("获得任务申请")
    @GetMapping("/getTaskApply")
    public RestResult getTaskApply(long taskId,long page,HttpServletRequest request){
        String account = jwtAuthService.getAccountFromToken(request);
        return collegeAdminService.collegeAdminGetTaskApply(taskId,page,account);
    }


    @ApiOperation("获得任务成员")
    @GetMapping("/getTaskMember")
    public RestResult getTaskMember(long taskId,long page,HttpServletRequest request){
        String account = jwtAuthService.getAccountFromToken(request);
        return collegeAdminService.collegeAdminGetTaskMember(taskId,page,account);
    }


    @ApiOperation("通过任务申请")
    @PutMapping("/passTaskApply")
    public RestResult passTaskApply(@RequestBody List<Long> ids, HttpServletRequest request){
        String account = jwtAuthService.getAccountFromToken(request);
        return collegeAdminService.collegeAdminPassTaskApply(ids,account);
    }


    @ApiOperation("不通过任务申请")
    @PutMapping("/unPassTaskApply")
    public RestResult unPassTaskApply(@RequestBody List<Long> ids, HttpServletRequest request){
        String account = jwtAuthService.getAccountFromToken(request);
        return collegeAdminService.collegeAdminUnPassTaskApply(ids,account);
    }


    @ApiOperation("完成任务")
    @PutMapping("/completeTask")
    public RestResult completeTask(@RequestBody Long taskId, HttpServletRequest request){
        String account = jwtAuthService.getAccountFromToken(request);
        return collegeAdminService.collegeAdminCompleteTask(taskId,account);
    }


    @ApiOperation("拒绝任务")
    @PutMapping("/rejectTaskMember")
    public RestResult rejectTaskMember(@RequestBody Long recordsId, HttpServletRequest request){
        String account = jwtAuthService.getAccountFromToken(request);
        return collegeAdminService.collegeAdminRejectTaskMember(recordsId,account);
    }


    @ApiOperation("发送任务通知")
    @PostMapping("/sendTaskNotify")
    public RestResult sendTaskNotify(@Validated @RequestBody NotifyVo notifyVo,HttpServletRequest request){
        String account = jwtAuthService.getAccountFromToken(request);
        return collegeAdminService.collegeAdminNotifyTaskMember(notifyVo,account);
    }

}
