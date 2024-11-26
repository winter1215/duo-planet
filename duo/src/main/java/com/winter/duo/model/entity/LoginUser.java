package com.winter.duo.model.entity;

import lombok.Data;

/**
 * 登录用户的脱敏信息( jwt payload 的实体)
 * @author winter
 * @create 2024-01-18 14:54
 */
@Data
public class LoginUser {
    private Long userId;
    private String username;
    private String role;
    private String createTime;
}
