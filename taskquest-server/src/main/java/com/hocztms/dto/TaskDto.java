package com.hocztms.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {
    private Long taskId;
    private Long collegeId;
    private String taskName;
    private String taskContent;
    private Integer type; //0代表抢单模式 1代表审核模式
    private Integer number;
    private Integer numberLimit;
    private Double points;
    private Date deadline;
}
