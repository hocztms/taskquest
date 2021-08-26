package com.hocztms.service.impl;

import com.hocztms.commons.RestResult;
import com.hocztms.entity.AdminEntity;
import com.hocztms.entity.AdminRoleEntity;
import com.hocztms.entity.CollegeEntity;
import com.hocztms.service.AdminService;
import com.hocztms.service.CollegeService;
import com.hocztms.service.SuperAdminService;
import com.hocztms.utils.ResultUtils;
import com.hocztms.vo.CollegeAdminVo;
import com.hocztms.vo.CollegeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SuperAdminServiceImpl implements SuperAdminService {

    @Autowired
    private CollegeService collegeService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public RestResult superAdminCreateCollege(CollegeVo collegeVo) {
        try {
            collegeService.insertCollege(new CollegeEntity(0,collegeVo.getCollegeName()));
            return ResultUtils.success();
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtils.systemError();
        }
    }

    @Override
    public RestResult superAdminCreateCollegeAdmin(CollegeAdminVo collegeAdminVo) {
        try {
            adminService.insertAdmin(new AdminEntity(collegeAdminVo.getUsername(),passwordEncoder.encode(collegeAdminVo.getPassword()),collegeAdminVo.getCollegeId()));
            adminService.insertAdminRole(new AdminRoleEntity(collegeAdminVo.getUsername(),"collegeAdmin"));
            return ResultUtils.success();
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtils.systemError();
        }
    }

    @Override
    public RestResult superAdminFindCollegeByPage(Long page) {
        try {

            return ResultUtils.success(collegeService.findCollegeByPage(page));
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtils.systemError();
        }
    }

    @Override
    public RestResult superAdminDeleteCollegeAdminByUsername(String username) {
        try {
            AdminEntity adminEntity = adminService.findAdminByUsername(username);

            if (adminEntity.getCollegeId()==-1){
                return ResultUtils.error("无权限");
            }

            adminService.deleteAdminByUsername(username);

            return ResultUtils.success();
        }catch (Exception e){

            e.printStackTrace();
            return ResultUtils.systemError();
        }
    }

    @Override
    public RestResult superAdminUpdateCollege(CollegeVo collegeVo) {
        try {
            CollegeEntity collegeEntity = new CollegeEntity(collegeVo.getCollegeId(),collegeVo.getCollegeName());
            collegeService.updateCollegeById(collegeEntity);

            return ResultUtils.success();
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtils.systemError();
        }
    }

    @Override
    public RestResult superAdminDeleteCollegeById(Long collegeId) {
        try {
            collegeService.deleteCollegeById(collegeId);


            return ResultUtils.success();
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtils.systemError();
        }
    }


}
