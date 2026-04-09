package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private boolean success;
    private String message;
    private Long userId;
    private String username;
    private String account;
    private String token;
    private boolean needUpdatePassword;  // 是否需要更新密码
    private String updateReason;          // 更新原因
}
