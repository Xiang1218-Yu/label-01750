package com.dorm.controller;

import com.dorm.annotation.OperationLog;
import com.dorm.annotation.RequireRole;
import com.dorm.common.PageResult;
import com.dorm.common.Result;
import com.dorm.entity.RepairRequest;
import com.dorm.service.RepairService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/repairs")
@RequiredArgsConstructor
public class RepairController {

    private final RepairService repairService;

    @GetMapping
    @RequireRole({1, 2}) // 管理员和宿管可查看所有维修申请
    public Result<PageResult<RepairRequest>> page(@RequestParam(defaultValue = "1") int current,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(required = false) Integer status) {
        return Result.success(repairService.page(current, size, status));
    }

    @GetMapping("/my")
    @RequireRole({1, 2, 3}) // 所有角色可查看自己的维修申请
    public Result<PageResult<RepairRequest>> myPage(@RequestParam(defaultValue = "1") int current,
                                                    @RequestParam(defaultValue = "10") int size) {
        return Result.success(repairService.myPage(current, size));
    }

    @PostMapping
    @RequireRole({1, 2, 3}) // 所有角色可提交维修申请
    @OperationLog(module = "维修管理", operation = "提交维修申请")
    public Result<Void> create(@RequestBody @Valid RepairRequest request) {
        repairService.create(request);
        return Result.success();
    }

    @PutMapping("/{id}/process")
    @RequireRole({1, 2}) // 管理员和宿管可处理维修申请
    @OperationLog(module = "维修管理", operation = "处理维修申请")
    public Result<Void> process(@PathVariable Long id,
                                @RequestParam Integer status,
                                @RequestParam(required = false) String reply) {
        repairService.process(id, status, reply);
        return Result.success();
    }
}
