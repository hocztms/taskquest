package com.hocztms.mqvo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperaLogs {
    private String operaModule;
    private String operaName;
    private String account;
    private String authorities;
    private Long collegeId;
    private String uri;
    private String ip;
    private String reqParam;
    private String resParam;
    private Date operaDate;

}
