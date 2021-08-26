package com.hocztms.controller;

import com.hocztms.commons.RestResult;
import com.hocztms.jwt.JwtAuthService;
import com.hocztms.service.StudentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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


    @ApiOperation("获得学院任务清单")
    @GetMapping("/getCollegeTask")
    public RestResult getCollegeTask(int page, HttpServletRequest request){
        String account = jwtAuthService.getAccountFromToken(request);
        return studentService.studentGetCollegeTask(account,page);
    }


    @ApiOperation("申请任务")
    @PostMapping("/applyTask")
    public RestResult applyTask(@RequestBody Long taskId,HttpServletRequest request){
        String account = jwtAuthService.getAccountFromToken(request);
        return studentService.studentApplyCollegeTask(taskId,account);
    }


    @ApiOperation("获得任务记录")
    @GetMapping("/getTaskRecords")
    public RestResult getTaskRecords(long page,HttpServletRequest request){
        String account = jwtAuthService.getAccountFromToken(request);

        return studentService.studentGetTaskRecords(account,page);
    }


    @ApiOperation("删除任务记录 （假删除）")
    @DeleteMapping("/deleteTaskRecords")
    public RestResult deleteTaskRecords(@RequestBody List<Long> ids, HttpServletRequest request){
        String account = jwtAuthService.getAccountFromToken(request);

        return studentService.studentDeleteTaskRecords(ids,account);
    }
}
