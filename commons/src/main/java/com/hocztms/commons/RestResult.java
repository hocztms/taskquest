package com.hocztms.commons;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

//已经测试
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestResult {
    private int code;
    private String msg;
    private Object data;

    @SuppressWarnings("unchecked")
    public void put(String key, Object data) {
        if (this.data == null) {
            this.data = new HashMap<String, Object>();
        }
        ((Map) this.data).put(key, data);
    }

}
