package com.dorm.controller;

import com.dorm.annotation.OperationLog;
import com.dorm.annotation.RequireRole;
import com.dorm.common.PageResult;
import com.dorm.common.Result;
import com.dorm.entity.SysUser;
import com.dorm.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@RequireRole({1}) // 仅管理员可访问
public class UserController {

    private final UserService userService;

    @GetMapping
    public Result<PageResult<SysUser>> page(@RequestParam(defaultValue = "1") int current,
                                            @RequestParam(defaultValue = "10") int size,
                                            @RequestParam(required = false) String username,
                                            @RequestParam(required = false) Integer role) {
        return Result.success(userService.page(current, size, username, role));
    }

    @PostMapping
    @OperationLog(module = "用户管理", operation = "新增用户")
    public Result<Void> create(@RequestBody @Valid SysUser user) {
        userService.create(user);
        return Result.success();
    }

    @PutMapping("/{id}")
    @OperationLog(module = "用户管理", operation = "修改用户")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Valid SysUser user) {
        user.setId(id);
        userService.update(user);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperationLog(module = "用户管理", operation = "删除用户")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    @OperationLog(module = "用户管理", operation = "修改用户状态")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return Result.success();
    }
}
