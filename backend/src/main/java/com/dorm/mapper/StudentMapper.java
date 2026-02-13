package com.dorm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {
    
    @Select("<script>" +
            "SELECT s.*, bd.bed_number, bd.id as bed_id, r.room_number, b.name as building_name " +
            "FROM student s " +
            "LEFT JOIN bed bd ON bd.student_id = s.id " +
            "LEFT JOIN room r ON bd.room_id = r.id " +
            "LEFT JOIN building b ON r.building_id = b.id " +
            "WHERE s.deleted = 0 " +
            "<if test='name != null and name != \"\"'> AND s.name LIKE CONCAT('%', #{name}, '%')</if>" +
            "<if test='studentNo != null and studentNo != \"\"'> AND s.student_no LIKE CONCAT('%', #{studentNo}, '%')</if>" +
            "ORDER BY s.id DESC" +
            "</script>")
    IPage<Student> selectPageWithInfo(Page<Student> page, @Param("name") String name, @Param("studentNo") String studentNo);
    
    @Select("SELECT s.*, bd.bed_number, bd.id as bed_id, r.room_number, b.name as building_name " +
            "FROM student s " +
            "LEFT JOIN bed bd ON bd.student_id = s.id " +
            "LEFT JOIN room r ON bd.room_id = r.id " +
            "LEFT JOIN building b ON r.building_id = b.id " +
            "WHERE s.user_id = #{userId} AND s.deleted = 0")
    Student selectByUserId(@Param("userId") Long userId);
}
