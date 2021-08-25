package com.hocztms.controller;

import com.hocztms.commons.RestResult;
import com.hocztms.jwt.JwtAuthService;
import com.hocztms.service.StudentService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/user")
@PreAuthorize("hasAuthority('student')")
@Api(tags = "学生方法接口，权限要求：学生")
public class StudentController {

    @Autowired
    private StudentService studentService;
    @Autowired
    private JwtAuthService jwtAuthService;

    @GetMapping("/getCollegeTask")
    public RestResult getCollegeTask(int page, HttpServletRequest request){
        String account = jwtAuthService.getAccountFromToken(request);
        return studentService.studentGetCollegeTask(account,page);
    }

    @PostMapping("/applyTask")
    public RestResult applyTask(@RequestBody Long taskId,HttpServletRequest request){
        String account = jwtAuthService.getAccountFromToken(request);
        return studentService.studentApplyCollegeTask(taskId,account);
    }

    @GetMapping("/getTaskRecords")
    public RestResult getTaskRecords(long page,HttpServletRequest request){
        String account = jwtAuthService.getAccountFromToken(request);

        return studentService.studentGetTaskRecords(account,page);
    }

    @DeleteMapping("/deleteTaskRecords")
    public RestResult deleteTaskRecords(@RequestBody List<Long> ids, HttpServletRequest request){
        String account = jwtAuthService.getAccountFromToken(request);

        return studentService.studentDeleteTaskRecords(ids,account);
    }
}
