package com.hocztms.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hocztms.dto.UserDto;
import com.hocztms.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {


    @Select("select * from tb_student,tb_user where tb_user.student_id = tb_student.student_id and open_id = #{openId}")
    UserDto selectUserDtoByOpenId(String openId);
}
