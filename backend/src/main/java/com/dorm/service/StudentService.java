package com.dorm.service;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.BusinessException;
import com.dorm.common.PageResult;
import com.dorm.entity.Bed;
import com.dorm.entity.Room;
import com.dorm.entity.Student;
import com.dorm.entity.SysUser;
import com.dorm.mapper.BedMapper;
import com.dorm.mapper.RoomMapper;
import com.dorm.mapper.StudentMapper;
import com.dorm.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentMapper studentMapper;
    private final SysUserMapper userMapper;
    private final BedMapper bedMapper;
    private final RoomMapper roomMapper;

    public PageResult<Student> page(int current, int size, String name, String studentNo) {
        Page<Student> page = new Page<>(current, size);
        return PageResult.of(studentMapper.selectPageWithInfo(page, name, studentNo));
    }

    @Transactional
    public void create(Student student) {
        if (studentMapper.selectCount(new LambdaQueryWrapper<Student>()
                .eq(Student::getStudentNo, student.getStudentNo())) > 0) {
            throw new BusinessException("学号已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(student.getStudentNo());
        user.setPassword(BCrypt.hashpw("123456"));
        user.setRealName(student.getName());
        user.setPhone(student.getPhone());
        user.setRole(3);
        user.setStatus(1);
        userMapper.insert(user);
        student.setUserId(user.getId());
        studentMapper.insert(student);
    }

    public void update(Student student) {
        Student existing = studentMapper.selectById(student.getId());
        if (existing == null) {
            throw new BusinessException("学生不存在");
        }
        if (!existing.getStudentNo().equals(student.getStudentNo()) &&
            studentMapper.selectCount(new LambdaQueryWrapper<Student>()
                .eq(Student::getStudentNo, student.getStudentNo())) > 0) {
            throw new BusinessException("学号已存在");
        }
        studentMapper.updateById(student);
        if (existing.getUserId() != null) {
            SysUser user = new SysUser();
            user.setId(existing.getUserId());
            user.setRealName(student.getName());
            user.setPhone(student.getPhone());
            userMapper.updateById(user);
        }
    }

    @Transactional
    public void delete(Long id) {
        Student student = studentMapper.selectById(id);
        if (student == null) return;
        bedMapper.update(null, new LambdaUpdateWrapper<Bed>()
                .eq(Bed::getStudentId, id)
                .set(Bed::getStudentId, null)
                .set(Bed::getStatus, 0));
        if (student.getUserId() != null) {
            userMapper.deleteById(student.getUserId());
        }
        studentMapper.deleteById(id);
    }

    @Transactional
    public void bindBed(Long studentId, Long bedId) {
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        Bed existingBed = bedMapper.selectOne(new LambdaQueryWrapper<Bed>()
                .eq(Bed::getStudentId, studentId));
        if (existingBed != null) {
            throw new BusinessException("该学生已有床位");
        }
        Bed bed = bedMapper.selectById(bedId);
        if (bed == null) {
            throw new BusinessException("床位不存在");
        }
        if (bed.getStatus() == 1) {
            throw new BusinessException("该床位已被占用");
        }
        bed.setStudentId(studentId);
        bed.setStatus(1);
        bedMapper.updateById(bed);
        Room room = roomMapper.selectById(bed.getRoomId());
        room.setCurrentCount(room.getCurrentCount() + 1);
        roomMapper.updateById(room);
    }

    @Transactional
    public void unbindBed(Long studentId) {
        Bed bed = bedMapper.selectOne(new LambdaQueryWrapper<Bed>()
                .eq(Bed::getStudentId, studentId));
        if (bed == null) {
            throw new BusinessException("该学生没有床位");
        }
        // 使用 LambdaUpdateWrapper 来更新，因为 updateById 不会更新 null 值
        bedMapper.update(null, new LambdaUpdateWrapper<Bed>()
                .eq(Bed::getId, bed.getId())
                .set(Bed::getStudentId, null)
                .set(Bed::getStatus, 0));
        Room room = roomMapper.selectById(bed.getRoomId());
        room.setCurrentCount(Math.max(0, room.getCurrentCount() - 1));
        roomMapper.updateById(room);
    }
}
