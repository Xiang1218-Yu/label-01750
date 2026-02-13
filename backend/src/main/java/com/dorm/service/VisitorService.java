package com.dorm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.BusinessException;
import com.dorm.common.PageResult;
import com.dorm.entity.Student;
import com.dorm.entity.VisitorRecord;
import com.dorm.mapper.StudentMapper;
import com.dorm.mapper.VisitorRecordMapper;
import com.dorm.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VisitorService {

    private final VisitorRecordMapper visitorMapper;
    private final StudentMapper studentMapper;

    public PageResult<VisitorRecord> page(int current, int size, Integer status) {
        Page<VisitorRecord> page = new Page<>(current, size);
        return PageResult.of(visitorMapper.selectPageWithInfo(page, status, null));
    }

    public PageResult<VisitorRecord> myPage(int current, int size) {
        Student student = studentMapper.selectByUserId(UserContext.getUserId());
        if (student == null) {
            throw new BusinessException("学生信息不存在");
        }
        Page<VisitorRecord> page = new Page<>(current, size);
        return PageResult.of(visitorMapper.selectPageWithInfo(page, null, student.getId()));
    }

    public void create(VisitorRecord record) {
        // 如果前端传了studentId，直接使用（管理员登记）
        // 否则根据当前登录用户获取学生信息（学生自己登记）
        if (record.getStudentId() == null) {
            Student student = studentMapper.selectByUserId(UserContext.getUserId());
            if (student == null) {
                throw new BusinessException("学生信息不存在");
            }
            record.setStudentId(student.getId());
        } else {
            // 验证学生是否存在
            Student student = studentMapper.selectById(record.getStudentId());
            if (student == null) {
                throw new BusinessException("被访学生不存在");
            }
        }
        record.setStatus(0);
        record.setVisitTime(LocalDateTime.now());
        visitorMapper.insert(record);
    }

    public void leave(Long id) {
        VisitorRecord record = visitorMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("访客记录不存在");
        }
        record.setStatus(1);
        record.setLeaveTime(LocalDateTime.now());
        visitorMapper.updateById(record);
    }
}
