package com.hocztms.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "tb_task")
public class TaskEntity implements Serializable {
    @TableId(value = "task_id",type = IdType.AUTO)
    private long taskId;
    private String publisher;
    private long collegeId;

    private String taskName;
    private String taskContent;
    private String phone;

    @TableField(value = "QQGoup")
    private String QQGroup;

    private Date deadline; // 截至日期
    private int type; //0代表抢单模式 1代表审核模式
    private int number;
    private int numberLimit;
    private double points;

    private int status;
    @Version
    private int version;
}
