package com.dorm.annotation;

import java.lang.annotation.*;

/**
 * 角色权限控制注解
 * role: 1-管理员, 2-宿管, 3-学生
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {
    /**
     * 允许访问的角色列表
     */
    int[] value();
}
