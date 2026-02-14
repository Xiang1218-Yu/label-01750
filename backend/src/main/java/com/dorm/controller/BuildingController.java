package com.dorm.controller;

import com.dorm.annotation.OperationLog;
import com.dorm.annotation.RequireRole;
import com.dorm.common.PageResult;
import com.dorm.common.Result;
import com.dorm.common.RoleConstants;
import com.dorm.entity.Building;
import com.dorm.service.BuildingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/buildings")
@RequiredArgsConstructor
@RequireRole({RoleConstants.ADMIN}) // 仅管理员可访问
public class BuildingController {

    private final BuildingService buildingService;

    @GetMapping
    public Result<PageResult<Building>> page(@RequestParam(defaultValue = "1") int current,
                                             @RequestParam(defaultValue = "10") int size,
                                             @RequestParam(required = false) String name,
                                             @RequestParam(required = false) Integer gender) {
        return Result.success(buildingService.page(current, size, name, gender));
    }

    @GetMapping("/list")
    @RequireRole({RoleConstants.ADMIN, RoleConstants.DORM_MANAGER}) // 管理员和宿管可获取楼栋列表（用于下拉选择）
    public Result<List<Building>> list() {
        return Result.success(buildingService.list());
    }

    @PostMapping
    @OperationLog(module = "楼栋管理", operation = "新增楼栋")
    public Result<Void> create(@RequestBody @Valid Building building) {
        buildingService.create(building);
        return Result.success();
    }

    @PutMapping("/{id}")
    @OperationLog(module = "楼栋管理", operation = "修改楼栋")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Valid Building building) {
        building.setId(id);
        buildingService.update(building);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperationLog(module = "楼栋管理", operation = "删除楼栋")
    public Result<Void> delete(@PathVariable Long id) {
        buildingService.delete(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    @OperationLog(module = "楼栋管理", operation = "修改楼栋状态")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        buildingService.updateStatus(id, status);
        return Result.success();
    }
}
