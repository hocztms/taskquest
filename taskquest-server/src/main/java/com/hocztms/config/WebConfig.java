package com.hocztms.config;

import com.hocztms.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private FileUtils fileUtils;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //资源映射
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + fileUtils.getPath());

    }

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        // 设置允许跨域的路由
//        registry.addMapping("/**")
//                // 设置允许跨域请求的域名
//                .allowedOriginPatterns("*")
//                // 是否允许证书（cookies）
//                .allowCredentials(true)
//                // 设置允许的方法
//                .allowedMethods("*")
//                // 跨域允许时间
//                .maxAge(3600);
//    }


    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        //测试websocket
        registry.addViewController("/client").setViewName("client");
        registry.addViewController("/index").setViewName("index");
    }


}