package com.dorm.interceptor;

import com.dorm.common.BusinessException;
import com.dorm.util.JwtUtil;
import com.dorm.util.UserContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new BusinessException(401, "未登录或token已过期");
        }
        token = token.substring(7);
        if (!jwtUtil.validateToken(token)) {
            throw new BusinessException(401, "token无效");
        }
        Claims claims = jwtUtil.parseToken(token);
        UserContext.setUserId(claims.get("userId", Long.class));
        UserContext.setUsername(claims.get("username", String.class));
        UserContext.setRole(claims.get("role", Integer.class));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
