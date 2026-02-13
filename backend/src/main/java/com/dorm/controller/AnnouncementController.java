package com.dorm.controller;

import com.dorm.annotation.OperationLog;
import com.dorm.common.PageResult;
import com.dorm.common.Result;
import com.dorm.entity.Announcement;
import com.dorm.service.AnnouncementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping
    public Result<PageResult<Announcement>> page(@RequestParam(defaultValue = "1") int current,
                                                 @RequestParam(defaultValue = "10") int size,
                                                 @RequestParam(required = false) Integer type,
                                                 @RequestParam(required = false) String title) {
        return Result.success(announcementService.page(current, size, type, title));
    }

    @GetMapping("/latest")
    public Result<List<Announcement>> latest(@RequestParam(defaultValue = "5") int limit) {
        return Result.success(announcementService.latest(limit));
    }

    @PostMapping
    @OperationLog(module = "公告管理", operation = "发布公告")
    public Result<Void> create(@RequestBody @Valid Announcement announcement) {
        announcementService.create(announcement);
        return Result.success();
    }

    @PutMapping("/{id}")
    @OperationLog(module = "公告管理", operation = "修改公告")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Valid Announcement announcement) {
        announcement.setId(id);
        announcementService.update(announcement);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperationLog(module = "公告管理", operation = "删除公告")
    public Result<Void> delete(@PathVariable Long id) {
        announcementService.delete(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    @OperationLog(module = "公告管理", operation = "修改公告状态")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        announcementService.updateStatus(id, status);
        return Result.success();
    }
}
