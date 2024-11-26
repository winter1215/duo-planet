package com.winter.duo.config.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author winter
 * @create 2024-01-15 17:10
 */
@ConfigurationProperties(prefix = "security")
@Configuration
@Data
public class SecurityConfiguration {
    private String jwtKey;
    private Long jwtExpired;
    private String[] whiteList;
}
