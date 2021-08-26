package com.hocztms.service;

import com.hocztms.commons.RestResult;
import com.hocztms.vo.CollegeAdminVo;
import com.hocztms.vo.CollegeVo;

public interface SuperAdminService {

    RestResult superAdminCreateCollege(CollegeVo collegeVo);

    RestResult superAdminCreateCollegeAdmin(CollegeAdminVo collegeAdminVo);

    RestResult superAdminFindCollegeByPage(Long page);

    RestResult superAdminDeleteCollegeAdminByUsername(String username);

    RestResult superAdminUpdateCollege(CollegeVo collegeVo);

    RestResult superAdminDeleteCollegeById(Long collegeId);
}
