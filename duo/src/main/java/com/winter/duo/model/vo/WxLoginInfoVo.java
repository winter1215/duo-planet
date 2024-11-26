package com.winter.duo.model.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @author winter
 * @create 2024-01-19 14:19
 */
@Data
@Builder
public class WxLoginInfoVo {
    private String url;
    /**
    * 验证码对应的票据
    */
    private String ticket;
    /**
    * 验证码
    */
    private String verifyCode;
}
