package com.dorm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.BusinessException;
import com.dorm.common.PageResult;
import com.dorm.entity.Bed;
import com.dorm.entity.RepairRequest;
import com.dorm.entity.Student;
import com.dorm.mapper.BedMapper;
import com.dorm.mapper.RepairRequestMapper;
import com.dorm.mapper.StudentMapper;
import com.dorm.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

@Service
@RequiredArgsConstructor
public class RepairService {

    private final RepairRequestMapper repairMapper;
    private final StudentMapper studentMapper;
    private final BedMapper bedMapper;

    public PageResult<RepairRequest> page(int current, int size, Integer status) {
        Page<RepairRequest> page = new Page<>(current, size);
        return PageResult.of(repairMapper.selectPageWithInfo(page, status, null));
    }

    public PageResult<RepairRequest> myPage(int current, int size) {
        Student student = studentMapper.selectByUserId(UserContext.getUserId());
        if (student == null) {
            throw new BusinessException("学生信息不存在");
        }
        Page<RepairRequest> page = new Page<>(current, size);
        return PageResult.of(repairMapper.selectPageWithInfo(page, null, student.getId()));
    }

    public void create(RepairRequest request) {
        // 如果前端传了studentId和roomId，直接使用（管理员代为申请）
        if (request.getStudentId() != null && request.getRoomId() != null) {
            request.setStatus(0);
            repairMapper.insert(request);
            return;
        }
        // 否则根据当前登录用户获取学生信息（学生自己申请）
        Student student = studentMapper.selectByUserId(UserContext.getUserId());
        if (student == null) {
            throw new BusinessException("学生信息不存在");
        }
        Bed bed = bedMapper.selectOne(new LambdaQueryWrapper<Bed>()
                .eq(Bed::getStudentId, student.getId()));
        if (bed == null) {
            throw new BusinessException("您还未分配宿舍");
        }
        request.setStudentId(student.getId());
        request.setRoomId(bed.getRoomId());
        request.setStatus(0);
        repairMapper.insert(request);
    }

    public void process(Long id, Integer status, String reply) {
        RepairRequest request = repairMapper.selectById(id);
        if (request == null) {
            throw new BusinessException("维修申请不存在");
        }
        request.setStatus(status);
        request.setReply(reply);
        repairMapper.updateById(request);
    }
}
