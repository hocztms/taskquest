package com.hocztms.service.impl;

import com.hocztms.entity.OperaLogEntity;
import com.hocztms.mapper.OperaLogMapper;
import com.hocztms.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogServiceImpl implements LogService {
    @Autowired
    private OperaLogMapper operaLogMapper;


    @Override
    public void insertOperaLog(OperaLogEntity operaLogEntity) {
        operaLogMapper.insert(operaLogEntity);
    }
}
