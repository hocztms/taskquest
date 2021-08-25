package com.hocztms.service;

import com.hocztms.entity.CollegeEntity;

import java.util.List;

public interface CollegeService {


    CollegeEntity insertCollege(CollegeEntity collegeEntity);

    List<CollegeEntity> findCollegeByPage(Long page);

    CollegeEntity updateCollegeById(CollegeEntity collegeEntity);

    void deleteCollegeById(Long collegeId);
}
