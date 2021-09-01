package com.hocztms.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class RedisUtils {

    List<Long> parseToLongList(Set objects){
        List<Integer> list = new ArrayList<>(objects);

        List<Long> longList = JSONArray.parseArray(list.toString(),Long.class);
        return longList;
    }

}
