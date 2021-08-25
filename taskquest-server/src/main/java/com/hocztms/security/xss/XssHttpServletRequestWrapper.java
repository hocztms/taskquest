package com.hocztms.security.xss;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {


    private Map<String, String[]> parameterMap;

    public XssHttpServletRequestWrapper() {
        super(new XssHttpServletRequestWrapper());
    }

    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        this.parameterMap = request.getParameterMap();
    }

    @Override
    public String getParameter(String name) {
        String[] values = parameterMap.get(name);
        if (values[0]==null) {
            return null;
        }
        String cleanedValue = XssUtils.cleanXss(values[0]);

        if (!cleanedValue.equals(values[0])) {
            log.warn("The request contains xss attack");
            log.info("The xss attack data before filter:" + values[0] + ", after" + cleanedValue);
        }

        return cleanedValue;
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);

        if (values==null)  {
            return null;
        }
        int count = values.length;
        String[] cleanedValues = new String[count];
        for (int i = 0; i < count; i++) {
            cleanedValues[i] = XssUtils.cleanXss(values[i]);

            if (!cleanedValues[i].equals(values[i])) {
                log.warn("The request contains xss attack");
                log.info("The xss attack data before filter:" + values[i] + ", after" + cleanedValues[i]);
            }
        }

        return cleanedValues;
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (value == null) {
            return null;
        }
        String cleanedValue = XssUtils.cleanXss(value);

        if (!cleanedValue.equals(value)) {
            log.warn("The request contains xss attack");
            log.info("The xss attack data before filter:" + value + ", after" + cleanedValue);
        }

        return cleanedValue;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (super.getContentType().contains("application/json"))
        {
            //判断json数据
            String string = getRequestBody(super.getInputStream());
            Object valuesObj = JSON.parse(string);
            if (valuesObj instanceof JSONObject)
            {
                //利用map过滤
                Map valuesMap = JSON.parseObject(string, Map.class);
                Map<Object, Object> resultMap = new HashMap<>(valuesMap.size());

                for (Object key : valuesMap.keySet()) {
                    Object val = valuesMap.get(key);
                    if (val instanceof String) {
                        resultMap.put(key, XssUtils.cleanXss(val.toString()));
                    } else {
                        resultMap.put(key, val);
                    }
                }
                string = JSON.toJSONString(resultMap);
            }else {
                string = XssUtils.cleanXss(string);
            }

            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8));
            return new ServletInputStream() {
                @Override
                public int read() {
                    return byteArrayInputStream.read();
                }

                @Override
                public boolean isFinished() {
                    return false;
                }

                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setReadListener(ReadListener listener) {
                }
            };
        }

        return super.getInputStream();
    }

    private String getRequestBody(InputStream stream) {
        String line;
        StringBuilder body = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));

        try {
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
        } catch (IOException e) {
            log.error("read request body fail");
        }
        return body.toString();
    }
}


