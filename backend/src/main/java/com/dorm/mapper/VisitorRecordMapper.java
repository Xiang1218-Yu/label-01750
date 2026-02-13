package com.dorm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.entity.VisitorRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface VisitorRecordMapper extends BaseMapper<VisitorRecord> {
    
    @Select("<script>" +
            "SELECT vr.*, s.name as student_name, CONCAT(b.name, ' ', r.room_number, '室') as room_info " +
            "FROM visitor_record vr " +
            "LEFT JOIN student s ON vr.student_id = s.id " +
            "LEFT JOIN bed bd ON bd.student_id = s.id " +
            "LEFT JOIN room r ON bd.room_id = r.id " +
            "LEFT JOIN building b ON r.building_id = b.id " +
            "WHERE 1=1 " +
            "<if test='status != null'> AND vr.status = #{status}</if>" +
            "<if test='studentId != null'> AND vr.student_id = #{studentId}</if>" +
            "ORDER BY vr.id DESC" +
            "</script>")
    IPage<VisitorRecord> selectPageWithInfo(Page<VisitorRecord> page, @Param("status") Integer status, @Param("studentId") Long studentId);
}
