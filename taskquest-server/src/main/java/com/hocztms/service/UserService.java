package com.hocztms.service;

import com.hocztms.commons.RestResult;
import com.hocztms.dto.UserDto;
import com.hocztms.entity.StudentEntity;
import com.hocztms.entity.UserEntity;
import com.hocztms.entity.UserRoleEntity;

import java.util.List;

public interface UserService {

    UserEntity findUserEntityByStudentId(Long studentId);

    UserEntity findUserByOpenId(String openId);

    UserDto findUserDtoByOpenId(String openId);

    UserEntity insertUser(UserEntity userEntity);

    List<UserRoleEntity> findUserRolesByOpenId(String openId);

    UserEntity updateUserByOpenId(UserEntity userEntity);

    UserRoleEntity insertUserRole(UserRoleEntity userRoleEntity);

    StudentEntity findStudentByStudentId(long studentId);
}
