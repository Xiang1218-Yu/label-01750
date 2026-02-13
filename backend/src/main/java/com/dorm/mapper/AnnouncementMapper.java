package com.dorm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {
    
    @Select("<script>" +
            "SELECT a.*, u.real_name as publisher_name " +
            "FROM announcement a " +
            "LEFT JOIN sys_user u ON a.publisher_id = u.id " +
            "WHERE a.deleted = 0 " +
            "<if test='type != null'> AND a.type = #{type}</if>" +
            "<if test='title != null and title != \"\"'> AND a.title LIKE CONCAT('%', #{title}, '%')</if>" +
            "ORDER BY a.id DESC" +
            "</script>")
    IPage<Announcement> selectPageWithInfo(Page<Announcement> page, @Param("type") Integer type, @Param("title") String title);
}
