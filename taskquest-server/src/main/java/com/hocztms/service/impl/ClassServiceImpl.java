package com.hocztms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hocztms.entity.ClassEntity;
import com.hocztms.mapper.ClassMapper;
import com.hocztms.service.ClassService;
import com.hocztms.utils.RedisPageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassServiceImpl implements ClassService {
    @Autowired
    private ClassMapper classMapper;

    @Override
    @CachePut(value = "c")
    public ClassEntity insertClass(ClassEntity classEntity) {
        classMapper.insert(classEntity);

        return classEntity;
    }

    @Override
    public void updateClass(ClassEntity classEntity) {
        classMapper.updateById(classEntity);
    }

    @Override
    public void deleteClass(ClassEntity classEntity) {
        classMapper.deleteById(classEntity.getId());
    }

    @Override
    public List<ClassEntity> findClassByCollegeId(Long collegeId, Long page) {
        QueryWrapper<ClassEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("college_id",collegeId);
        return classMapper.selectPage(new Page<>(page, RedisPageUtils.PAGE_SIZE),wrapper).getRecords();
    }
}
