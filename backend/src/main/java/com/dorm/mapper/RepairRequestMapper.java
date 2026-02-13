package com.dorm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.entity.RepairRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RepairRequestMapper extends BaseMapper<RepairRequest> {
    
    @Select("<script>" +
            "SELECT rr.*, s.name as student_name, CONCAT(b.name, ' ', r.room_number, '室') as room_info " +
            "FROM repair_request rr " +
            "LEFT JOIN student s ON rr.student_id = s.id " +
            "LEFT JOIN room r ON rr.room_id = r.id " +
            "LEFT JOIN building b ON r.building_id = b.id " +
            "WHERE 1=1 " +
            "<if test='status != null'> AND rr.status = #{status}</if>" +
            "<if test='studentId != null'> AND rr.student_id = #{studentId}</if>" +
            "ORDER BY rr.id DESC" +
            "</script>")
    IPage<RepairRequest> selectPageWithInfo(Page<RepairRequest> page, @Param("status") Integer status, @Param("studentId") Long studentId);
}
