package com.hocztms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hocztms.entity.AdminEntity;
import com.hocztms.entity.AdminRoleEntity;
import com.hocztms.mapper.admin.AdminMapper;
import com.hocztms.mapper.admin.AdminRoleMapper;
import com.hocztms.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private AdminRoleMapper adminRoleMapper;

    @Override
    @CachePut(value = "admin",key = "#result.username")
    public AdminEntity insertAdmin(AdminEntity adminEntity) {
        adminMapper.insert(adminEntity);
        return adminEntity;
    }

    @Override
    @CacheEvict(value = "adminRole",key = "#result.username")
    public AdminRoleEntity insertAdminRole(AdminRoleEntity adminRoleEntity) {
        adminRoleMapper.insert(adminRoleEntity);
        return adminRoleEntity;
    }

    @Override
    @Cacheable(value = "adminRole",key = "#username")
    public List<AdminRoleEntity> findAdminRolesByUsername(String username) {
        QueryWrapper<AdminRoleEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return adminRoleMapper.selectList(wrapper);
    }

    @Override
    @Cacheable(value = "admin",key = "#username")
    public AdminEntity findAdminByUsername(String username) {
        QueryWrapper<AdminEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return adminMapper.selectOne(wrapper);
    }

    @Override
    @CacheEvict(value = "admin",key = "#username")
    public void deleteAdminByUsername(String username) {
        QueryWrapper<AdminEntity>wrapper = new QueryWrapper<>();
        wrapper.eq("username",username);
        adminMapper.delete(wrapper);
    }
}
