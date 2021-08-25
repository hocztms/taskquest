package com.hocztms.controller;

import com.hocztms.commons.RestResult;
import com.hocztms.jwt.JwtAuthService;
import com.hocztms.service.TaskService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@PreAuthorize("hasAnyAuthority('collegeAdmin','student')")
@RequestMapping("/task")
@Api(tags = "任务方法接口，权限要求：学生或学院管理员")
public class TaskController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private JwtAuthService jwtAuthService;

    @GetMapping("/getInfo")
    public RestResult getTaskInfo(long taskId, HttpServletRequest request){
        String account = jwtAuthService.getAccountFromToken(request);
        return taskService.getTaskInfo(taskId,account);
    }

    @GetMapping("/getTaskByKeyword")
    public RestResult getTaskByKeyword(String keyword){
        return taskService.findTaskDtoByKeyword(keyword);
    }
}
