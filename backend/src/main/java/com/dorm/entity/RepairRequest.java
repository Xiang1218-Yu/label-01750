package com.dorm.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("repair_request")
public class RepairRequest {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private Long roomId;
    private String title;
    private String description;
    private String images;
    private Integer status;
    private String reply;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    @TableField(exist = false)
    private String studentName;
    @TableField(exist = false)
    private String roomInfo;
}
