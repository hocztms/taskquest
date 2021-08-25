package com.hocztms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hocztms.entity.MessageEntity;
import com.hocztms.mapper.MessageMapper;
import com.hocztms.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {


    @Autowired
    private MessageMapper messageMapper;
    @Override
    @CacheEvict(value = "userMessage",key = "#messageEntity.openId")
    @CachePut(value = "message",key = "#result.id")
    public MessageEntity insertMessage(MessageEntity messageEntity) {
        messageMapper.insert(messageEntity);

        return messageEntity;
    }

    @Override
    @CacheEvict(value = "userMessage",key = "#messageEntity.openId")
    @CachePut(value = "message",key = "#result.id")
    public MessageEntity updateMessageById(MessageEntity messageEntity) {
        messageMapper.updateById(messageEntity);
        return messageEntity;
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "userMessage",key = "#messageEntity.openId"),
                    @CacheEvict(value = "message",key = "#messageEntity.id")
            }
    )
    public void deleteMessageById(MessageEntity messageEntity) {
        messageMapper.deleteById(messageEntity.getId());
    }

    @Override
    @Cacheable(value = "message",key = "#id")
    public MessageEntity findMessageById(Long id) {
        return messageMapper.selectById(id);
    }

    @Override
    @Cacheable(value = "userMessage",key = "#openId")
    public List<MessageEntity> findMessageByOpenId(String openId) {
        QueryWrapper<MessageEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("open_id",openId);
        wrapper.orderByAsc("read_tag");
        return messageMapper.selectList(wrapper);
    }
}
