package com.hocztms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hocztms.mqvo.ExceptLogs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "tb_exceptLog")
public class ExceptLogEntity extends ExceptLogs {

    @TableId(value = "except_id",type = IdType.AUTO)
    private Long exceptId;
}
