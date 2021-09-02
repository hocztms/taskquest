package com.hocztms.service;

import com.hocztms.entity.ExceptLogEntity;
import com.hocztms.entity.OperaLogEntity;

public interface LogService {

    void insertOperaLog(OperaLogEntity operaLogEntity);

    void insertExceptLog(ExceptLogEntity exceptLogEntity);
}
