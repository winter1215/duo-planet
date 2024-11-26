package com.winter.duo;

import cn.hutool.jwt.JWT;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 主类测试

 */
//@SpringBootTest
class MainApplicationTests {

    @Test
    void contextLoads() {
//        ChatGPT chatGPT = ChatGPT.builder()
//                .apiKey("")
//                .apiHost("https://cfwus02.opapi.win/")
//                .build()
//                .init();
//        String ans = chatGPT.chat("鲁迅为什么暴打周树人");
//        System.out.println(ans);
        String key = "U@vY42qlgq9ASXLXNqfze%Jj-NQ!ZKrX$iKonXy1u^8F021c7bzo@t5$ANwWCor#";
        long expireDate = 5 * 60 * 1000; //Token有效期5分钟
        String code = "13900000000"; //手机号码
        Date date = new Date(System.currentTimeMillis() + expireDate);
//        Algorithm algorithm = Algorithm.HMAC256(key);
        Map<String, Object> header = new HashMap<String, Object>();
        header.put("Type", "Jwt");
        header.put("alg", "HS256");
//        String token= JWT.create()
//                .withHeader(header)
//                .withClaim("code", code)
//                .withExpiresAt(date)
//                .sign(algorithm);
//        String token = JWTUtil.createToken(header, key.getBytes(StandardCharsets.UTF_8));
       String token = JWT.create().setExpiresAt(date).setPayload("code", code).setKey(key.getBytes(StandardCharsets.UTF_8)).sign();
        System.out.println(token);
    }

}
