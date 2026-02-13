package com.dorm.controller;

import com.dorm.annotation.OperationLog;
import com.dorm.annotation.RequireRole;
import com.dorm.common.PageResult;
import com.dorm.common.Result;
import com.dorm.entity.VisitorRecord;
import com.dorm.service.VisitorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/visitors")
@RequiredArgsConstructor
public class VisitorController {

    private final VisitorService visitorService;

    @GetMapping
    @RequireRole({1, 2}) // 管理员和宿管可查看所有访客
    public Result<PageResult<VisitorRecord>> page(@RequestParam(defaultValue = "1") int current,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(required = false) Integer status) {
        return Result.success(visitorService.page(current, size, status));
    }

    @GetMapping("/my")
    @RequireRole({1, 2}) // 管理员和宿管可查看
    public Result<PageResult<VisitorRecord>> myPage(@RequestParam(defaultValue = "1") int current,
                                                    @RequestParam(defaultValue = "10") int size) {
        return Result.success(visitorService.myPage(current, size));
    }

    @PostMapping
    @RequireRole({1, 2}) // 管理员和宿管可登记访客
    @OperationLog(module = "访客管理", operation = "登记访客")
    public Result<Void> create(@RequestBody @Valid VisitorRecord record) {
        visitorService.create(record);
        return Result.success();
    }

    @PutMapping("/{id}/leave")
    @RequireRole({1, 2}) // 管理员和宿管可操作离开
    @OperationLog(module = "访客管理", operation = "访客离开")
    public Result<Void> leave(@PathVariable Long id) {
        visitorService.leave(id);
        return Result.success();
    }
}
