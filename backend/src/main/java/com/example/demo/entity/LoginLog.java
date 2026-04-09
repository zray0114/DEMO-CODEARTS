package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("login_log")
public class LoginLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String account;
    
    private LocalDateTime loginTime;
    
    private String loginStatus;
    
    private String loginIp;
    
    private String loginMessage;
    
    private String userAgent;
}
