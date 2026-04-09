package com.example.demo.service;

import com.example.demo.config.SecurityConfig;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.entity.LoginLog;
import com.example.demo.entity.User;
import com.example.demo.repository.LoginLogRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private LoginLogRepository loginLogRepository;
    
    @Autowired
    private SecurityConfig securityConfig;
    
    @Transactional
    public LoginResponse login(LoginRequest request, String ip, String userAgent) {
        String account = request.getAccount();
        String password = request.getPassword();
        
        // 查找用户
        User user = userRepository.findByAccount(account).orElse(null);
        
        if (user == null) {
            saveLoginLog(null, account, "FAILED", ip, "账户不存在", userAgent);
            return new LoginResponse(false, "账户或密码错误", null, null, null, null, false, null);
        }
        
        // 验证密码
        if (!user.getPassword().equals(password)) {
            saveLoginLog(user.getId(), account, "FAILED", ip, "密码错误", userAgent);
            return new LoginResponse(false, "账户或密码错误", null, null, null, null, false, null);
        }
        
        // 检查是否需要更新密码
        boolean needUpdate = false;
        String updateReason = null;
        
        // 检查是否使用默认密码
        if (securityConfig.getDefaultPassword().equals(password)) {
            needUpdate = true;
            updateReason = "DEFAULT_PASSWORD";
        }
        
        // 检查密码是否过期（超过6个月）
        if (!needUpdate && user.getLastLoginTime() != null) {
            LocalDateTime expireTime = user.getLastLoginTime().plusMonths(securityConfig.getPasswordExpireMonths());
            if (LocalDateTime.now().isAfter(expireTime)) {
                needUpdate = true;
                updateReason = "PASSWORD_EXPIRED";
            }
        }
        
        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);
        
        // 生成token
        String token = UUID.randomUUID().toString();
        
        // 记录成功日志
        saveLoginLog(user.getId(), account, "SUCCESS", ip, "登录成功", userAgent);
        
        return new LoginResponse(true, "登录成功", user.getId(), user.getUsername(), 
            user.getAccount(), token, needUpdate, updateReason);
    }
    
    // 更新密码
    @Transactional
    public boolean updatePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }
        user.setPassword(newPassword);
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);
        return true;
    }
    
    // 注册用户
    @Transactional
    public Map<String, Object> register(String account, String password, String username) {
        Map<String, Object> result = new java.util.HashMap<>();
        
        // 检查账户是否已存在
        if (userRepository.findByAccount(account).isPresent()) {
            result.put("success", false);
            result.put("message", "账户已存在");
            return result;
        }
        
        // 创建新用户
        User user = new User();
        user.setAccount(account);
        user.setPassword(password);
        user.setUsername(username != null && !username.isEmpty() ? username : account);
        user.setLastLoginTime(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        
        result.put("success", true);
        result.put("message", "注册成功");
        result.put("userId", savedUser.getId());
        
        return result;
    }
    
    // 检查账户是否存在
    public boolean checkAccountExists(String account) {
        return userRepository.findByAccount(account).isPresent();
    }
    
    private void saveLoginLog(Long userId, String account, String status, String ip, String message, String userAgent) {
        LoginLog log = new LoginLog();
        log.setUserId(userId);
        log.setAccount(account);
        log.setLoginStatus(status);
        log.setLoginIp(ip);
        log.setLoginMessage(message);
        log.setUserAgent(userAgent);
        loginLogRepository.save(log);
    }
}
