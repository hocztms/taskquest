package com.hocztms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "tb_taskRecords")
public class TaskRecords {

    @TableId(value = "id",type = IdType.AUTO)
    private long id;
    private long taskId;
    private String openId;
    private long studentId;
    private String studentName;
    private String email;
    private int status;
    private int deleted;
}
