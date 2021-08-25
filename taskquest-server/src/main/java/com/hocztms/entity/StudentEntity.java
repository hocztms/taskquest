package com.hocztms.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.parameters.P;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "tb_student")
public class StudentEntity {
    private long studentId;
    private String password;
    private String studentName;
    private long classId;
    private long collegeId;
}
