package com.winter.duo.config.ai;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author winter
 * @create 2024-01-26 17:18
 */
@Data
@Configuration
@ConfigurationProperties("chatgpt")
public class ChatGptConfigration {
    private String host;
    private String[] keys;
}
