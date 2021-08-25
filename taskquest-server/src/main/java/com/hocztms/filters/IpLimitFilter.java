package com.hocztms.filters;

import com.alibaba.fastjson.JSONObject;
import com.hocztms.commons.RestResult;
import com.hocztms.utils.RedisIpUtils;
import com.hocztms.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
@Slf4j
public class IpLimitFilter extends OncePerRequestFilter {

    @Autowired
    private RedisIpUtils redisIpUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();
        log.info("ip: {} requestURI: {}",ip,uri);

        if(uri.contains("socket")){
            chain.doFilter(request,response);
            return;
        }

        if (redisIpUtils.checkIpLimit(ip)){
            redisIpUtils.incIpLimit(ip);

            if (redisIpUtils.checkUriLimit(uri)){
                redisIpUtils.incUriLimit(uri);

                chain.doFilter(request,response);
                return;
            }
            else {
                redisIpUtils.banUri(uri);

                log.warn("uri: {} 访问频繁....",uri);
            }
        }

        else {
            redisIpUtils.banIp(ip);
            log.warn("ip: {} 访问太过频繁 ...",ip);
        }


        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setHeader("Content-type", "application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        RestResult result = ResultUtils.error("服务器繁忙 请稍后再试");
        writer.write(JSONObject.toJSONString(result));
        writer.flush();
        writer.close();
    }
}
