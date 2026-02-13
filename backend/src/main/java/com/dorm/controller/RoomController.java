package com.dorm.controller;

import com.dorm.annotation.OperationLog;
import com.dorm.annotation.RequireRole;
import com.dorm.common.PageResult;
import com.dorm.common.Result;
import com.dorm.entity.Room;
import com.dorm.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@RequireRole({1, 2}) // 管理员和宿管可访问
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public Result<PageResult<Room>> page(@RequestParam(defaultValue = "1") int current,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(required = false) Long buildingId,
                                         @RequestParam(required = false) String roomNumber) {
        return Result.success(roomService.page(current, size, buildingId, roomNumber));
    }

    @GetMapping("/{id}")
    public Result<Room> detail(@PathVariable Long id) {
        return Result.success(roomService.getDetail(id));
    }

    @PostMapping
    @OperationLog(module = "房间管理", operation = "新增房间")
    public Result<Void> create(@RequestBody @Valid Room room) {
        roomService.create(room);
        return Result.success();
    }

    @PutMapping("/{id}")
    @OperationLog(module = "房间管理", operation = "修改房间")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Valid Room room) {
        room.setId(id);
        roomService.update(room);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperationLog(module = "房间管理", operation = "删除房间")
    public Result<Void> delete(@PathVariable Long id) {
        roomService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}/beds")
    public Result<?> getBeds(@PathVariable Long id) {
        return Result.success(roomService.getBeds(id));
    }

    @PutMapping("/{id}/status")
    @OperationLog(module = "房间管理", operation = "修改房间状态")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        roomService.updateStatus(id, status);
        return Result.success();
    }
}
