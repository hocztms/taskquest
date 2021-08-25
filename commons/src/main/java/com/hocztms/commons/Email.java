package com.hocztms.commons;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Email implements Serializable {
    private String to;
    private String subject;
    private String content;
    private Date date;
    private String code;
}
