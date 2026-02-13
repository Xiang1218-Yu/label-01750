package com.dorm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.BusinessException;
import com.dorm.common.PageResult;
import com.dorm.entity.Bed;
import com.dorm.entity.Room;
import com.dorm.mapper.BedMapper;
import com.dorm.mapper.RoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomMapper roomMapper;
    private final BedMapper bedMapper;

    public PageResult<Room> page(int current, int size, Long buildingId, String roomNumber) {
        Page<Room> page = new Page<>(current, size);
        IPage<Room> result = roomMapper.selectPageWithInfo(page, buildingId, roomNumber);
        // 为每个房间加载床位信息
        for (Room room : result.getRecords()) {
            room.setBeds(bedMapper.selectByRoomId(room.getId()));
        }
        return PageResult.of(result);
    }

    public Room getDetail(Long id) {
        Room room = roomMapper.selectById(id);
        if (room != null) {
            room.setBeds(bedMapper.selectByRoomId(id));
        }
        return room;
    }

    @Transactional
    public void create(Room room) {
        // 检查同一楼栋内房间号是否重复
        long count = roomMapper.selectCount(new LambdaQueryWrapper<Room>()
                .eq(Room::getBuildingId, room.getBuildingId())
                .eq(Room::getRoomNumber, room.getRoomNumber()));
        if (count > 0) {
            throw new BusinessException("该楼栋已存在相同房间号");
        }
        roomMapper.insert(room);
        for (int i = 1; i <= room.getCapacity(); i++) {
            Bed bed = new Bed();
            bed.setRoomId(room.getId());
            bed.setBedNumber(String.valueOf(i));
            bed.setStatus(0);
            bedMapper.insert(bed);
        }
    }

    public void update(Room room) {
        // 检查同一楼栋内房间号是否重复（排除自己）
        Room existing = roomMapper.selectById(room.getId());
        if (existing == null) {
            throw new BusinessException("房间不存在");
        }
        if (!existing.getRoomNumber().equals(room.getRoomNumber()) || 
            !existing.getBuildingId().equals(room.getBuildingId())) {
            long count = roomMapper.selectCount(new LambdaQueryWrapper<Room>()
                    .eq(Room::getBuildingId, room.getBuildingId())
                    .eq(Room::getRoomNumber, room.getRoomNumber())
                    .ne(Room::getId, room.getId()));
            if (count > 0) {
                throw new BusinessException("该楼栋已存在相同房间号");
            }
        }
        roomMapper.updateById(room);
    }

    public void delete(Long id) {
        long occupiedCount = bedMapper.selectCount(new LambdaQueryWrapper<Bed>()
                .eq(Bed::getRoomId, id)
                .eq(Bed::getStatus, 1));
        if (occupiedCount > 0) {
            throw new BusinessException("该房间有学生入住，无法删除");
        }
        bedMapper.delete(new LambdaQueryWrapper<Bed>().eq(Bed::getRoomId, id));
        roomMapper.deleteById(id);
    }

    public java.util.List<Bed> getBeds(Long roomId) {
        return bedMapper.selectByRoomId(roomId);
    }

    public void updateStatus(Long id, Integer status) {
        Room room = new Room();
        room.setId(id);
        room.setStatus(status);
        roomMapper.updateById(room);
    }
}
