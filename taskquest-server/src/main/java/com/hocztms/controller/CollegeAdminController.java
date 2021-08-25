package com.hocztms.controller;

import com.hocztms.commons.RestResult;
import com.hocztms.jwt.JwtAuthService;
import com.hocztms.service.CollegeAdminService;
import com.hocztms.vo.ClassVo;
import com.hocztms.vo.NotifyVo;
import com.hocztms.vo.TaskVo;
import io.swagger.annotations.Api;
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

    @PostMapping("/createClass")
    public RestResult createClass(@Validated@RequestBody ClassVo classVo){
        return collegeAdminService.collegeAdminCreateClass(classVo);
    }

    @PostMapping("/createTask")
    public RestResult createTask(@Validated@RequestBody TaskVo taskVo, HttpServletRequest request){
        String account = jwtAuthService.getAccountFromToken(request);
        return collegeAdminService.collegeAdminCreateTask(taskVo,account);
    }

    @PutMapping("/updateTask")
    public RestResult updateTask(@Validated@RequestBody TaskVo taskVo, HttpServletRequest request){
        String account = jwtAuthService.getAccountFromToken(request);
        return collegeAdminService.collegeAdminUpdateTask(taskVo,account);
    }

    @GetMapping("/getTaskApply")
    public RestResult getTaskApply(long taskId,long page,HttpServletRequest request){
        String account = jwtAuthService.getAccountFromToken(request);
        return collegeAdminService.collegeAdminGetTaskApply(taskId,page,account);
    }

    @GetMapping("/getTaskMember")
    public RestResult getTaskMember(long taskId,long page,HttpServletRequest request){
        String account = jwtAuthService.getAccountFromToken(request);
        return collegeAdminService.collegeAdminGetTaskMember(taskId,page,account);
    }

    @PutMapping("/passTaskApply")
    public RestResult passTaskApply(@RequestBody List<Long> ids, HttpServletRequest request){
        String account = jwtAuthService.getAccountFromToken(request);
        return collegeAdminService.collegeAdminPassTaskApply(ids,account);
    }

    @PutMapping("/unPassTaskApply")
    public RestResult unPassTaskApply(@RequestBody List<Long> ids, HttpServletRequest request){
        String account = jwtAuthService.getAccountFromToken(request);
        return collegeAdminService.collegeAdminUnPassTaskApply(ids,account);
    }

    @PutMapping("/completeTask")
    public RestResult completeTask(@RequestBody Long taskId, HttpServletRequest request){
        String account = jwtAuthService.getAccountFromToken(request);
        return collegeAdminService.collegeAdminCompleteTask(taskId,account);
    }


    @PutMapping("/rejectTaskMember")
    public RestResult rejectTaskMember(@RequestBody Long recordsId, HttpServletRequest request){
        String account = jwtAuthService.getAccountFromToken(request);
        return collegeAdminService.collegeAdminRejectTaskMember(recordsId,account);
    }

    @PostMapping("/sendTaskNotify")
    public RestResult sendTaskNotify(@Validated @RequestBody NotifyVo notifyVo,HttpServletRequest request){
        String account = jwtAuthService.getAccountFromToken(request);
        return collegeAdminService.collegeAdminNotifyTaskMember(notifyVo,account);
    }

}
