package com.dorm.aspect;

import com.dorm.annotation.RequireRole;
import com.dorm.common.BusinessException;
import com.dorm.util.UserContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
@Order(1)
public class RoleCheckAspect {

    @Around("@within(com.dorm.annotation.RequireRole) || @annotation(com.dorm.annotation.RequireRole)")
    public Object checkRole(ProceedingJoinPoint point) throws Throwable {
        Integer currentRole = UserContext.getRole();
        if (currentRole == null) {
            throw new BusinessException(401, "未登录");
        }
        
        // 优先获取方法上的注解，其次获取类上的注解
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        RequireRole requireRole = method.getAnnotation(RequireRole.class);
        if (requireRole == null) {
            requireRole = point.getTarget().getClass().getAnnotation(RequireRole.class);
        }
        
        if (requireRole != null) {
            int[] allowedRoles = requireRole.value();
            boolean hasPermission = Arrays.stream(allowedRoles)
                    .anyMatch(role -> role == currentRole);
            if (!hasPermission) {
                throw new BusinessException(403, "无权限访问");
            }
        }
        
        return point.proceed();
    }
}
