package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String ip = getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        return authService.login(request, ip, userAgent);
    }
    
    @PostMapping("/update-password")
    public ResponseEntity<Map<String, Object>> updatePassword(@RequestBody Map<String, Object> request) {
        Long userId = Long.parseLong(request.get("userId").toString());
        String newPassword = request.get("newPassword").toString();
        
        boolean success = authService.updatePassword(userId, newPassword);
        
        return ResponseEntity.ok(Map.of(
            "success", success,
            "message", success ? "密码更新成功" : "密码更新失败"
        ));
    }
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, Object> request) {
        String account = request.get("account").toString();
        String password = request.get("password").toString();
        String username = request.get("username") != null ? request.get("username").toString() : null;
        
        Map<String, Object> result = authService.register(account, password, username);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/check-account")
    public ResponseEntity<Map<String, Object>> checkAccount(@RequestParam String account) {
        boolean exists = authService.checkAccountExists(account);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
    
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
