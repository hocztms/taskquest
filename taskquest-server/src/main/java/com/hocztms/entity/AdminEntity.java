package com.hocztms.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//用来存放 学校管理员 学院管理员2个假帐号
@TableName(value = "tb_admin")
public class AdminEntity {
    private String username;
    private String password;
    private long collegeId;
}
