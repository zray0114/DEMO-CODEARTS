package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "login_log")
public class LoginLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column
    private String account;
    
    @Column(name = "login_time")
    private LocalDateTime loginTime;
    
    @Column(name = "login_status")
    private String loginStatus;
    
    @Column(name = "login_ip")
    private String loginIp;
    
    @Column(name = "login_message")
    private String loginMessage;
    
    @Column(name = "user_agent")
    private String userAgent;
    
    @PrePersist
    protected void onCreate() {
        if (loginTime == null) {
            loginTime = LocalDateTime.now();
        }
    }
}
