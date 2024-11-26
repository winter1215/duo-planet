package com.winter.duo.config.ai;

import com.plexpt.chatgpt.ChatGPT;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author winter
 * @create 2024-01-26 17:13
 */
@Component
public class ChatGptConfig {
    @Resource
    private ChatGptConfigration chatGptConfigration;

    @Bean
    public ChatGPT chatGPT() {
        return ChatGPT.builder()
                .apiHost(chatGptConfigration.getHost())
                .apiKey(chatGptConfigration.getKeys()[0])
                .build()
                .init();
    }
}
