package com.hocztms.utils;

import com.hocztms.commons.RestResult;
import org.springframework.stereotype.Component;

@Component
public class ResultUtils {

    public static RestResult success(Object data) {
        RestResult result = new RestResult();
        result.setCode(1);
        result.setMsg("success");
        result.setData(data);
        return result;
    }

    public static RestResult success(String msg, Object data) {
        RestResult result = new RestResult();
        result.setCode(1);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    public static RestResult success() {
        return success(null);
    }

    public static RestResult error(int code, String msg) {
        RestResult result = new RestResult();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    public static RestResult error(String msg) {
        RestResult result = new RestResult();
        result.setCode(0);
        result.setMsg(msg);
        return result;
    }

    public static RestResult systemError() {
        RestResult result = new RestResult();
        result.setCode(-1);
        result.setMsg("error");
        return result;
    }
}
