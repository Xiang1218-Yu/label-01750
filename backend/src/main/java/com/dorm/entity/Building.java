package com.dorm.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("building")
public class Building {
    @TableId(type = IdType.AUTO)
    private Long id;
    @NotBlank(message = "楼栋名称不能为空")
    private String name;
    private String description;
    private Integer gender;
    private Long managerId;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
    
    @TableField(exist = false)
    private String managerName;
    @TableField(exist = false)
    private Integer roomCount;
}
