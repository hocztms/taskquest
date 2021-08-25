package com.hocztms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hocztms.dto.UserDto;
import com.hocztms.entity.StudentEntity;
import com.hocztms.entity.UserEntity;
import com.hocztms.entity.UserRoleEntity;
import com.hocztms.mapper.StudentMapper;
import com.hocztms.mapper.user.UserMapper;
import com.hocztms.mapper.user.UserRoleMapper;
import com.hocztms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private StudentMapper studentMapper;


    @Override
    //这里简单缓存 openId 与studentId的绑定 openId 与studentId绑定后不允许修改
    @Cacheable(value = "userStudent",key = "#studentId")
    public UserEntity findUserEntityByStudentId(Long studentId) {
        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId);
        return userMapper.selectOne(wrapper);
    }

    @Override
    @Cacheable(value = "user",key = "#openId")
    public UserEntity findUserByOpenId(String openId) {
        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("open_id", openId);
        return userMapper.selectOne(wrapper);
    }

    @Override
    @Cacheable(value = "userDto",key = "#openId")
    public UserDto findUserDtoByOpenId(String openId) {
        System.out.println("走数据库。。。。");
        return userMapper.selectUserDtoByOpenId(openId);
    }

    @Override
    @Cacheable(value = "userRole",key = "#openId")
    public List<UserRoleEntity> findUserRolesByOpenId(String openId) {
        QueryWrapper<UserRoleEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("open_id", openId);
        return userRoleMapper.selectList(wrapper);
    }

    @Override
    @CachePut(value = "user",key = "#result.openId")
    public UserEntity insertUser(UserEntity userEntity) {
        userMapper.insert(userEntity);
        return userEntity;
    }

    @Override
    @CacheEvict(value = "userDto",key = "#userEntity.openId")
    @CachePut(value = "user",key = "#result.openId")
    public UserEntity updateUserByOpenId(UserEntity userEntity) {
        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("open_id", userEntity.getOpenId());
        int update = userMapper.update(userEntity, wrapper);
        if (update==0){
            throw new RuntimeException("更新失败");
        }
        return userEntity;
    }

    @Override
    @CacheEvict(value = "userRole",key = "#result.openId")
    public UserRoleEntity insertUserRole(UserRoleEntity userRoleEntity) {
        userRoleMapper.insert(userRoleEntity);
        return userRoleEntity;
    }

    @Override
    @Cacheable(value = "student",key = "#studentId")
    public StudentEntity findStudentByStudentId(long studentId) {
        QueryWrapper<StudentEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId);
        return studentMapper.selectOne(wrapper);
    }

}
