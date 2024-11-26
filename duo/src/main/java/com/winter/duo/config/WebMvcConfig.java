package com.winter.duo.config;

import com.winter.duo.config.security.SecurityConfiguration;
import com.winter.duo.config.interceptor.TokenHandlerInterceptor;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvc配置 : 静态文件、拦截器等
 * @author winter
 * @create 2024-01-15 17:06
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final TokenHandlerInterceptor tokenHandlerInterceptor;
    private final SecurityConfiguration securityConfiguration;

    /**
    * 跨域配置
    */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 覆盖所有请求
        registry.addMapping("/**")
                // 允许发送 Cookie
                .allowCredentials(true)
                // 放行哪些域名（必须用 patterns，否则 * 会和 allowCredentials 冲突）
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("*");
    }

    /**
    * 拦截器配置
    */
    @Override
    public void addInterceptors(@NotNull InterceptorRegistry registry) {
        registry.addInterceptor(tokenHandlerInterceptor)
                // 不包括 context-path
                .excludePathPatterns(securityConfiguration.getWhiteList());
    }
}
