package com.hocztms.controller;

import com.hocztms.commons.RestResult;
import com.hocztms.jwt.JwtTokenUtils;
import com.hocztms.service.impl.RedisService;
import com.hocztms.service.SuperAdminService;
import com.hocztms.vo.CollegeAdminVo;
import com.hocztms.vo.CollegeVo;
import io.swagger.annotations.Api;
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

    @GetMapping("/test")
    public String test(String username, HttpServletRequest request){
        jwtTokenUtils.getAuthAccountFromToken(request.getHeader("token"));
        return  redisService.getUserBlackDate(username).toString();
    }

    @PostMapping("/createCollege")
    public RestResult createCollege(@Validated @RequestBody CollegeVo collegeVo){
        return superAdminService.superAdminCreateCollege(collegeVo);
    }

    @PutMapping("/updateCollege")
    public RestResult updateCollege(@Validated @RequestBody CollegeVo collegeVo){
        return superAdminService.superAdminUpdateCollege(collegeVo);
    }

    @DeleteMapping("/deleteCollege")
    public RestResult deleteCollege(@RequestBody Long collegeId){
        return superAdminService.superAdminDeleteCollegeById(collegeId);
    }

    @PostMapping("/createCollegeAdmin")
    public RestResult createCollegeAdmin(@Validated @RequestBody CollegeAdminVo collegeAdminVo){
        return superAdminService.superAdminCreateCollegeAdmin(collegeAdminVo);
    }
}
