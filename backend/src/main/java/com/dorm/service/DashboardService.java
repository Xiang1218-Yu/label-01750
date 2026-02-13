package com.dorm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dorm.entity.*;
import com.dorm.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final BuildingMapper buildingMapper;
    private final RoomMapper roomMapper;
    private final BedMapper bedMapper;
    private final StudentMapper studentMapper;
    private final RepairRequestMapper repairMapper;
    private final VisitorRecordMapper visitorMapper;

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("buildingCount", buildingMapper.selectCount(null));
        stats.put("roomCount", roomMapper.selectCount(null));
        stats.put("totalBeds", bedMapper.selectCount(null));
        stats.put("occupiedBeds", bedMapper.selectCount(new LambdaQueryWrapper<Bed>().eq(Bed::getStatus, 1)));
        stats.put("studentCount", studentMapper.selectCount(null));
        stats.put("pendingRepairs", repairMapper.selectCount(new LambdaQueryWrapper<RepairRequest>().eq(RepairRequest::getStatus, 0)));
        stats.put("visitingCount", visitorMapper.selectCount(new LambdaQueryWrapper<VisitorRecord>().eq(VisitorRecord::getStatus, 0)));
        return stats;
    }
}
