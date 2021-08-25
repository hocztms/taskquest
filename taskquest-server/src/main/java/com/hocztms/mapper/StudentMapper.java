package com.hocztms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hocztms.entity.StudentEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentMapper extends BaseMapper<StudentEntity> {
}
