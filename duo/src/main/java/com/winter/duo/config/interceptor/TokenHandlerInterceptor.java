package com.winter.duo.config.interceptor;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.JWTValidator;
import com.winter.duo.config.security.RequestContext;
import com.winter.duo.config.security.SecurityConfiguration;
import com.winter.duo.model.entity.LoginUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * Token拦截器
 * @author winter
 * @create 2024-01-15 17:04
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenHandlerInterceptor  implements HandlerInterceptor {
    private final SecurityConfiguration securityConfiguration;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        log.info("path: {}", request.getRequestURI());
        String token = request.getHeader("token");
        String jwtKey = securityConfiguration.getJwtKey();
        // 检查 token 是否合法
        if (StringUtils.isEmpty(token) || !JWTUtil.verify(token, jwtKey.getBytes(StandardCharsets.UTF_8))) {
            throw new ValidateException();
        }
        JWTValidator.of(token).validateDate();

        LoginUser loginUser = JWTUtil.parseToken(token).getPayloads().toBean(LoginUser.class);
        RequestContext.setRequestData(loginUser);
        return true;
    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception ex) throws Exception {
        // 清除 ThreadLocal, 防止内存泄露
        RequestContext.remove();
    }
}
