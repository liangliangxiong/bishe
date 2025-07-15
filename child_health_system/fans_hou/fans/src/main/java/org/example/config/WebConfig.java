package org.example.config;

import org.example.interceptors.LoginInterceptor;
import org.example.interceptors.PermissionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;
    
    @Autowired
    private PermissionInterceptor permissionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 登录拦截器
        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns("/user/login", "/user/register");
        
        // 权限拦截器
        registry.addInterceptor(permissionInterceptor)
                .excludePathPatterns("/user/login", "/user/register");
    }
}
