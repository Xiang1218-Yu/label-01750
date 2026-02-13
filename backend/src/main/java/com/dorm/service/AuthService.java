package com.dorm.service;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dorm.common.BusinessException;
import com.dorm.entity.Student;
import com.dorm.entity.SysUser;
import com.dorm.mapper.StudentMapper;
import com.dorm.mapper.SysUserMapper;
import com.dorm.util.JwtUtil;
import com.dorm.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysUserMapper userMapper;
    private final StudentMapper studentMapper;
    private final JwtUtil jwtUtil;

    public Map<String, Object> login(String username, String password) {
        SysUser user = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("realName", user.getRealName());
        result.put("role", user.getRole());
        return result;
    }

    public Map<String, Object> getUserInfo() {
        Long userId = UserContext.getUserId();
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("realName", user.getRealName());
        result.put("role", user.getRole());
        result.put("phone", user.getPhone());
        if (user.getRole() == 3) {
            Student student = studentMapper.selectByUserId(userId);
            if (student != null) {
                result.put("student", student);
            }
        }
        return result;
    }

    public void changePassword(String oldPassword, String newPassword) {
        Long userId = UserContext.getUserId();
        SysUser user = userMapper.selectById(userId);
        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        user.setPassword(BCrypt.hashpw(newPassword));
        userMapper.updateById(user);
    }
}
