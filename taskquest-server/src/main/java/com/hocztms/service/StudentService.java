package com.hocztms.service;

import com.hocztms.commons.RestResult;

import java.util.List;

public interface StudentService {

    RestResult studentGetCollegeTask(String openId,Integer page);

    RestResult studentApplyCollegeTask(Long taskId,String openId);

    RestResult studentGrabCollegeTask(Long taskId,String openId);

    RestResult studentGetTaskRecords(String openId,Long page);

    RestResult studentDeleteTaskRecords(List<Long> ids , String openId);
}
