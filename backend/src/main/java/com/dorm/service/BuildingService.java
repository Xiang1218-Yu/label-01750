package com.dorm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.BusinessException;
import com.dorm.common.PageResult;
import com.dorm.entity.Building;
import com.dorm.entity.Room;
import com.dorm.mapper.BuildingMapper;
import com.dorm.mapper.RoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BuildingService {

    private final BuildingMapper buildingMapper;
    private final RoomMapper roomMapper;

    public PageResult<Building> page(int current, int size, String name, Integer gender) {
        Page<Building> page = new Page<>(current, size);
        return PageResult.of(buildingMapper.selectPageWithInfo(page, name, gender));
    }

    public List<Building> list() {
        return buildingMapper.selectList(new LambdaQueryWrapper<Building>()
                .eq(Building::getStatus, 1)
                .orderByAsc(Building::getId));
    }

    public void create(Building building) {
        // 检查楼栋名称是否重复
        long count = buildingMapper.selectCount(new LambdaQueryWrapper<Building>()
                .eq(Building::getName, building.getName()));
        if (count > 0) {
            throw new BusinessException("楼栋名称已存在");
        }
        buildingMapper.insert(building);
    }

    public void update(Building building) {
        // 检查楼栋名称是否重复（排除自己）
        Building existing = buildingMapper.selectById(building.getId());
        if (existing == null) {
            throw new BusinessException("楼栋不存在");
        }
        if (!existing.getName().equals(building.getName())) {
            long count = buildingMapper.selectCount(new LambdaQueryWrapper<Building>()
                    .eq(Building::getName, building.getName())
                    .ne(Building::getId, building.getId()));
            if (count > 0) {
                throw new BusinessException("楼栋名称已存在");
            }
        }
        buildingMapper.updateById(building);
    }

    public void delete(Long id) {
        long roomCount = roomMapper.selectCount(new LambdaQueryWrapper<Room>()
                .eq(Room::getBuildingId, id));
        if (roomCount > 0) {
            throw new BusinessException("该楼栋下存在房间，无法删除");
        }
        buildingMapper.deleteById(id);
    }

    public void updateStatus(Long id, Integer status) {
        Building building = new Building();
        building.setId(id);
        building.setStatus(status);
        buildingMapper.updateById(building);
    }
}
