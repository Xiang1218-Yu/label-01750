package com.dorm.aspect;

import cn.hutool.json.JSONUtil;
import com.dorm.annotation.OperationLog;
import com.dorm.entity.SysOperationLog;
import com.dorm.mapper.SysOperationLogMapper;
import com.dorm.util.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final SysOperationLogMapper logMapper;

    @Around("@annotation(operationLog)")
    public Object around(ProceedingJoinPoint point, OperationLog operationLog) throws Throwable {
        Object result = point.proceed();
        try {
            saveLog(point, operationLog);
        } catch (Exception e) {
            log.error("保存操作日志失败", e);
        }
        return result;
    }

    private void saveLog(ProceedingJoinPoint point, OperationLog operationLog) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        SysOperationLog logEntity = new SysOperationLog();
        logEntity.setUserId(UserContext.getUserId());
        logEntity.setUsername(UserContext.getUsername());
        logEntity.setModule(operationLog.module());
        logEntity.setOperation(operationLog.operation());
        logEntity.setMethod(signature.getDeclaringTypeName() + "." + signature.getName());
        logEntity.setParams(JSONUtil.toJsonStr(point.getArgs()));
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            logEntity.setIp(getIpAddress(request));
        }
        logMapper.insert(logEntity);
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
