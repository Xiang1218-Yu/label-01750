package com.dorm.controller;

import com.dorm.annotation.OperationLog;
import com.dorm.common.PageResult;
import com.dorm.common.Result;
import com.dorm.entity.Student;
import com.dorm.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    public Result<PageResult<Student>> page(@RequestParam(defaultValue = "1") int current,
                                            @RequestParam(defaultValue = "10") int size,
                                            @RequestParam(required = false) String name,
                                            @RequestParam(required = false) String studentNo) {
        return Result.success(studentService.page(current, size, name, studentNo));
    }

    @PostMapping
    @OperationLog(module = "学生管理", operation = "新增学生")
    public Result<Void> create(@RequestBody @Valid Student student) {
        studentService.create(student);
        return Result.success();
    }

    @PutMapping("/{id}")
    @OperationLog(module = "学生管理", operation = "修改学生")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Valid Student student) {
        student.setId(id);
        studentService.update(student);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperationLog(module = "学生管理", operation = "删除学生")
    public Result<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return Result.success();
    }

    @PostMapping("/{id}/bindBed")
    @OperationLog(module = "学生管理", operation = "分配床位")
    public Result<Void> bindBed(@PathVariable Long id, @RequestParam Long bedId) {
        studentService.bindBed(id, bedId);
        return Result.success();
    }

    @PostMapping("/{id}/unbindBed")
    @OperationLog(module = "学生管理", operation = "退宿")
    public Result<Void> unbindBed(@PathVariable Long id) {
        studentService.unbindBed(id);
        return Result.success();
    }
}
