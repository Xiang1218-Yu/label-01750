package com.dorm.controller;

import com.dorm.annotation.OperationLog;
import com.dorm.annotation.RequireRole;
import com.dorm.common.Result;
import com.dorm.common.RoleConstants;
import com.dorm.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Data
    public static class LoginRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;
        @NotBlank(message = "密码不能为空")
        private String password;
    }

    @Data
    public static class ChangePasswordRequest {
        @NotBlank(message = "原密码不能为空")
        private String oldPassword;
        @NotBlank(message = "新密码不能为空")
        private String newPassword;
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request.getUsername(), request.getPassword()));
    }

    @PostMapping("/logout")
    @RequireRole({RoleConstants.ADMIN, RoleConstants.DORM_MANAGER, RoleConstants.STUDENT}) // 所有登录用户可访问
    public Result<Void> logout() {
        return Result.success();
    }

    @GetMapping("/info")
    @RequireRole({RoleConstants.ADMIN, RoleConstants.DORM_MANAGER, RoleConstants.STUDENT}) // 所有登录用户可访问
    public Result<Map<String, Object>> info() {
        return Result.success(authService.getUserInfo());
    }

    @PutMapping("/password")
    @RequireRole({RoleConstants.ADMIN, RoleConstants.DORM_MANAGER, RoleConstants.STUDENT}) // 所有登录用户可访问
    @OperationLog(module = "认证", operation = "修改密码")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request.getOldPassword(), request.getNewPassword());
        return Result.success();
    }
}
