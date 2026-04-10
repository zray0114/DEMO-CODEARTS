package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("fee_shares")
public class FeeShare {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long tenantId;
    
    private String shareMonth;
    
    private BigDecimal waterFee;
    
    private BigDecimal electricityFee;
    
    private BigDecimal gasFee;
    
    private BigDecimal propertyFee;
    
    private BigDecimal totalFee;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
