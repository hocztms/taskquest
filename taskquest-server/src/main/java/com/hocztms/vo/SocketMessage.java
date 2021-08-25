package com.hocztms.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//用来封装socket 信息
public class SocketMessage {
    private int code; //0 为消息提醒 1 为任务处理同步进度
    private Object message;
}
