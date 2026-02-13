package com.dorm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.entity.Building;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BuildingMapper extends BaseMapper<Building> {
    
    @Select("<script>" +
            "SELECT b.*, u.real_name as manager_name, " +
            "(SELECT COUNT(*) FROM room r WHERE r.building_id = b.id AND r.deleted = 0) as room_count " +
            "FROM building b " +
            "LEFT JOIN sys_user u ON b.manager_id = u.id " +
            "WHERE b.deleted = 0 " +
            "<if test='name != null and name != \"\"'> AND b.name LIKE CONCAT('%', #{name}, '%')</if>" +
            "<if test='gender != null'> AND b.gender = #{gender}</if>" +
            "ORDER BY b.id DESC" +
            "</script>")
    IPage<Building> selectPageWithInfo(Page<Building> page, @Param("name") String name, @Param("gender") Integer gender);
}
