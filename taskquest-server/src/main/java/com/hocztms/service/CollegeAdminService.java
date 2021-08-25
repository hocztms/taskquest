package com.hocztms.service;

import com.hocztms.commons.RestResult;
import com.hocztms.vo.ClassVo;
import com.hocztms.vo.NotifyVo;
import com.hocztms.vo.TaskVo;

import java.util.List;

public interface CollegeAdminService {
    RestResult collegeAdminCreateClass(ClassVo classVo);

    RestResult collegeAdminUpdateClass(ClassVo classVo);

    RestResult collegeAdminCreateTask(TaskVo taskVo,String username);

    RestResult collegeAdminUpdateTask(TaskVo taskVo,String username);

    RestResult collegeAdminGetTaskApply(Long taskId,Long page,String username);

    RestResult collegeAdminGetTaskMember(Long taskId,Long page,String username);

    RestResult collegeAdminUnPassTaskApply(List<Long> recordsIds,String username);

    RestResult collegeAdminPassTaskApply(List<Long> recordsIds,String username);

    RestResult collegeAdminCompleteTask(Long taskId,String username);

    RestResult collegeAdminRejectTaskMember(Long recordsId,String username);

    RestResult collegeAdminNotifyTaskMember(NotifyVo notifyVo,String username);

}
