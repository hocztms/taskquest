package com.hocztms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hocztms.entity.MessageEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<MessageEntity> {
}
