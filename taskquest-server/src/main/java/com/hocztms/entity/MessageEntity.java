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
@TableName(value = "tb_message")
public class MessageEntity {

    @TableId(value = "id",type = IdType.AUTO)
    private long id;
    private String openId;

    private String subject;
    private String content;
    private int readTag;
}
