package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("electricity_fees")
public class ElectricityFee {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private LocalDate paymentDate;
    
    private BigDecimal amount;
    
    private byte[] images;
    
    private String remark;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
