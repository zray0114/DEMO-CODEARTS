package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("menus")
public class Menu {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String path;
    
    private String icon;
    
    private Integer sortOrder;
    
    private Long parentId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
