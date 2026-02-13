package com.dorm.service;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dorm.common.BusinessException;
import com.dorm.entity.SysUser;
import com.dorm.mapper.StudentMapper;
import com.dorm.mapper.SysUserMapper;
import com.dorm.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private SysUserMapper userMapper;

    @Mock
    private StudentMapper studentMapper;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private SysUser testUser;

    @BeforeEach
    void setUp() {
        testUser = new SysUser();
        testUser.setId(1L);
        testUser.setUsername("admin");
        testUser.setPassword(BCrypt.hashpw("123456"));
        testUser.setRealName("管理员");
        testUser.setRole(1);
        testUser.setStatus(1);
    }

    @Test
    void login_WithValidCredentials_ShouldReturnToken() {
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);
        when(jwtUtil.generateToken(1L, "admin", 1)).thenReturn("test-token");

        Map<String, Object> result = authService.login("admin", "123456");

        assertNotNull(result);
        assertEquals("test-token", result.get("token"));
        assertEquals(1L, result.get("userId"));
        assertEquals("admin", result.get("username"));
    }

    @Test
    void login_WithInvalidUsername_ShouldThrowException() {
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, 
            () -> authService.login("invalid", "123456"));
        
        assertEquals("用户名或密码错误", exception.getMessage());
    }

    @Test
    void login_WithInvalidPassword_ShouldThrowException() {
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);

        BusinessException exception = assertThrows(BusinessException.class, 
            () -> authService.login("admin", "wrongpassword"));
        
        assertEquals("用户名或密码错误", exception.getMessage());
    }

    @Test
    void login_WithDisabledUser_ShouldThrowException() {
        testUser.setStatus(0);
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);

        BusinessException exception = assertThrows(BusinessException.class, 
            () -> authService.login("admin", "123456"));
        
        assertEquals("账号已被禁用", exception.getMessage());
    }
}
