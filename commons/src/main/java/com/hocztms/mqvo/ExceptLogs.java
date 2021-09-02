package com.hocztms.mqvo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExceptLogs {

    private String account;
    private String authorities;
    private String uri;
    private String ip;
    private String exceptName;
    private String exceptMsg;
    private String reqParam;
    private Date exceptDate;
}
