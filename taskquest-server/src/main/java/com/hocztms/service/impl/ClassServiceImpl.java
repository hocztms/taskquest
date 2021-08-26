package com.hocztms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hocztms.config.cacheResolvers.ClassPageCacheResolver;
import com.hocztms.entity.ClassEntity;
import com.hocztms.mapper.ClassMapper;
import com.hocztms.service.ClassService;
import com.hocztms.utils.RedisPageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassServiceImpl implements ClassService {
    @Autowired
    private ClassMapper classMapper;
    @Autowired
    private CacheManager cacheManager;

    @Override
    @CachePut(value = "class",key = "#result.id")
    @CacheEvict(cacheResolver = "ClassPageCacheResolver",allEntries = true)
    public ClassEntity insertClass(ClassEntity classEntity) {
        classMapper.insert(classEntity);

        return classEntity;
    }

    @Override
    @CachePut(value = "class",key = "#classEntity.id")
    @CacheEvict(cacheResolver = "ClassPageCacheResolver",allEntries = true)
    public void updateClass(ClassEntity classEntity) {
        classMapper.updateById(classEntity);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "class",key = "#classEntity.id"),
            @CacheEvict(cacheResolver = "ClassPageCacheResolver",allEntries = true)
    })
    public void deleteClass(ClassEntity classEntity) {
        classMapper.deleteById(classEntity.getId());
    }

    @Override
    @Cacheable(value = "class",key ="#id" )
    public ClassEntity findClassEntityById(Long id) {
        return classMapper.selectById(id);
    }

    @Override
    @Cacheable(cacheResolver = "ClassPageCacheResolver",key = "#page")
    public List<ClassEntity> findClassByCollegeId(Long collegeId, Long page) {
        QueryWrapper<ClassEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("college_id",collegeId);
        return classMapper.selectPage(new Page<>(page, RedisPageUtils.PAGE_SIZE),wrapper).getRecords();
    }


    @Bean("ClassPageCacheResolver")
    public ClassPageCacheResolver classPageCacheResolver(){
        return new ClassPageCacheResolver(cacheManager);
    }
}
