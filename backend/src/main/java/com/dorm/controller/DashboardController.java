package com.dorm.controller;

import com.dorm.annotation.RequireRole;
import com.dorm.common.Result;
import com.dorm.common.RoleConstants;
import com.dorm.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@RequireRole({RoleConstants.ADMIN, RoleConstants.DORM_MANAGER, RoleConstants.STUDENT}) // 所有角色可访问
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        return Result.success(dashboardService.getStats());
    }
}
