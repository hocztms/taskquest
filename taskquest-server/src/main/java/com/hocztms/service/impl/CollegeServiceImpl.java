package com.hocztms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hocztms.entity.CollegeEntity;
import com.hocztms.mapper.CollegeMapper;
import com.hocztms.service.CollegeService;
import com.hocztms.utils.RedisPageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollegeServiceImpl implements CollegeService {
    @Autowired
    private CollegeMapper collegeMapper;


    @Override
    @CacheEvict(value = "college",allEntries = true)
    public CollegeEntity insertCollege(CollegeEntity collegeEntity) {
        collegeMapper.insert(collegeEntity);
        return collegeEntity;
    }

    @Override
    @Cacheable(value = "college",key = "#page")
    public List<CollegeEntity> findCollegeByPage(Long page) {
        QueryWrapper<CollegeEntity> wrapper = new QueryWrapper<>();

        return collegeMapper.selectPage(new Page<>(page, RedisPageUtils.PAGE_SIZE),wrapper).getRecords();
    }


    @Override
    @CacheEvict(value = "college",allEntries = true)
    public CollegeEntity updateCollegeById(CollegeEntity collegeEntity) {
        collegeMapper.updateById(collegeEntity);
        return collegeEntity;
    }

    @Override
    @CacheEvict(value = "college",allEntries = true)
    public void deleteCollegeById(Long collegeId) {
        collegeMapper.deleteById(collegeId);
    }
}
