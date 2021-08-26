package com.hocztms.controller;

import com.alibaba.fastjson.JSONObject;
import com.hocztms.commons.RestResult;
import com.hocztms.jwt.JwtTokenUtils;
import com.hocztms.service.impl.RedisService;
import com.hocztms.service.SuperAdminService;
import com.hocztms.vo.CollegeAdminVo;
import com.hocztms.vo.CollegeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@PreAuthorize("hasAuthority('superAdmin')")
@RequestMapping("/superAdmin")
@Api(tags = "超级管理员接口，权限要求：超级管理员")
public class SuperAdminController {
    @Autowired
    private RedisService redisService;
    @Autowired
    private JwtTokenUtils jwtTokenUtils;
    @Autowired
    private SuperAdminService superAdminService;

//    @GetMapping("/test")
//    public String test(String username, HttpServletRequest request){
//        jwtTokenUtils.getAuthAccountFromToken(request.getHeader("token"));
//        return  redisService.getUserBlackDate(username).toString();
//    }

    @ApiOperation("创建学院")
    @PostMapping("/createCollege")
    public RestResult createCollege(@Validated @RequestBody CollegeVo collegeVo){
        return superAdminService.superAdminCreateCollege(collegeVo);
    }

    @ApiOperation("修改学院")
    @PutMapping("/updateCollege")
    public RestResult updateCollege(@Validated @RequestBody CollegeVo collegeVo){
        return superAdminService.superAdminUpdateCollege(collegeVo);
    }

    @ApiOperation("删除学院")
    @DeleteMapping("/deleteCollege")
    public RestResult deleteCollege(@RequestBody Long collegeId){
        return superAdminService.superAdminDeleteCollegeById(collegeId);
    }


    @ApiOperation("创建学院管理员假帐号")
    @PostMapping("/createCollegeAdmin")
    public RestResult createCollegeAdmin(@Validated @RequestBody CollegeAdminVo collegeAdminVo){
        return superAdminService.superAdminCreateCollegeAdmin(collegeAdminVo);
    }

    @ApiOperation("删除学院管理员账号")
    @DeleteMapping("/deleteCollegeAdmin")
    public RestResult deleteCollegeAdmin(
            @ApiParam(value = "格式 {\"username\":\"username\"}") @RequestBody String json){
        String username = (String) JSONObject.parseObject(json).get("username");
        return superAdminService.superAdminDeleteCollegeAdminByUsername(username);
    }


    @ApiOperation("查看学院列表")
    @GetMapping("/getCollege")
    public RestResult getCollegePage(long page){
        return superAdminService.superAdminFindCollegeByPage(page);
    }
}
