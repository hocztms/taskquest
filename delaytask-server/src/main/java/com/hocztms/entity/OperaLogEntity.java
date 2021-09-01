package com.hocztms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hocztms.mqvo.OperaLogs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "tb_operaLog")
public class OperaLogEntity extends OperaLogs {

    @TableId(value = "log_id",type = IdType.AUTO)
    private Long logId;

    private String operaModule;
    private String operaName;
    private String account;
    private String authorities;
    private String uri;
    private String ip;
    private String reqParam;
    private String resParam;
    private Date operaDate;

}
