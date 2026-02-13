package com.dorm.controller;

import com.dorm.annotation.OperationLog;
import com.dorm.common.Result;
import com.dorm.service.AuthService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestParam @NotBlank(message = "用户名不能为空") String username,
                                              @RequestParam @NotBlank(message = "密码不能为空") String password) {
        return Result.success(authService.login(username, password));
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }

    @GetMapping("/info")
    public Result<Map<String, Object>> info() {
        return Result.success(authService.getUserInfo());
    }

    @PutMapping("/password")
    @OperationLog(module = "认证", operation = "修改密码")
    public Result<Void> changePassword(@RequestParam @NotBlank(message = "原密码不能为空") String oldPassword,
                                        @RequestParam @NotBlank(message = "新密码不能为空") String newPassword) {
        authService.changePassword(oldPassword, newPassword);
        return Result.success();
    }
}
