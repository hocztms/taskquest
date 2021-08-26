package com.hocztms.service;

import com.hocztms.entity.ClassEntity;

import java.util.List;

public interface ClassService {

    ClassEntity insertClass(ClassEntity classEntity);

    void updateClass(ClassEntity classEntity);

    void deleteClass(ClassEntity classEntity);

    ClassEntity findClassEntityById(Long id);

    List<ClassEntity> findClassByCollegeId(Long collegeId,Long page);
}
