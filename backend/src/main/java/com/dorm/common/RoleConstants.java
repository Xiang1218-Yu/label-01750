package com.dorm.common;

/**
 * 角色常量定义
 */
public final class RoleConstants {
    private RoleConstants() {}
    
    /** 管理员 */
    public static final int ADMIN = 1;
    /** 宿管 */
    public static final int DORM_MANAGER = 2;
    /** 学生 */
    public static final int STUDENT = 3;
    
    /** 所有角色 */
    public static final int[] ALL = {ADMIN, DORM_MANAGER, STUDENT};
    /** 管理员和宿管 */
    public static final int[] ADMIN_AND_MANAGER = {ADMIN, DORM_MANAGER};
}
