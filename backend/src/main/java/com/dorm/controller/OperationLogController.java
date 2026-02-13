package com.dorm.controller;

import com.dorm.annotation.RequireRole;
import com.dorm.common.PageResult;
import com.dorm.common.Result;
import com.dorm.entity.SysOperationLog;
import com.dorm.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
@RequireRole({1}) // 仅管理员可访问
public class OperationLogController {

    private final OperationLogService logService;

    @GetMapping
    public Result<PageResult<SysOperationLog>> page(@RequestParam(defaultValue = "1") int current,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    @RequestParam(required = false) String username,
                                                    @RequestParam(required = false) String module) {
        return Result.success(logService.page(current, size, username, module));
    }
}
