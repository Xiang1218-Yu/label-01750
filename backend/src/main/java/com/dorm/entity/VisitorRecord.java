package com.dorm.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("visitor_record")
public class VisitorRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private String visitorName;
    private String visitorPhone;
    private String idCard;
    private String reason;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime visitTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime leaveTime;
    private Integer status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @TableField(exist = false)
    private String studentName;
    @TableField(exist = false)
    private String roomInfo;
}
