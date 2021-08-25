package com.hocztms.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "tb_user")
public class UserEntity {
    private String openId;
    private Long studentId;
    private String phone;
    private String email;
    private int status;
    private double point;
    @Version
    private int version;
}
