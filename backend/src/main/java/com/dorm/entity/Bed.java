package com.dorm.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("bed")
public class Bed {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long roomId;
    private String bedNumber;
    private Long studentId;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    @TableField(exist = false)
    private String studentName;
    @TableField(exist = false)
    private String studentNo;
}
