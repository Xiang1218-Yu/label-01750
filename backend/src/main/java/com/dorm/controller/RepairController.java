package com.dorm.controller;

import com.dorm.annotation.OperationLog;
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
    public Result<PageResult<RepairRequest>> page(@RequestParam(defaultValue = "1") int current,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(required = false) Integer status) {
        return Result.success(repairService.page(current, size, status));
    }

    @GetMapping("/my")
    public Result<PageResult<RepairRequest>> myPage(@RequestParam(defaultValue = "1") int current,
                                                    @RequestParam(defaultValue = "10") int size) {
        return Result.success(repairService.myPage(current, size));
    }

    @PostMapping
    @OperationLog(module = "维修管理", operation = "提交维修申请")
    public Result<Void> create(@RequestBody @Valid RepairRequest request) {
        repairService.create(request);
        return Result.success();
    }

    @PutMapping("/{id}/process")
    @OperationLog(module = "维修管理", operation = "处理维修申请")
    public Result<Void> process(@PathVariable Long id,
                                @RequestParam Integer status,
                                @RequestParam(required = false) String reply) {
        repairService.process(id, status, reply);
        return Result.success();
    }
}
