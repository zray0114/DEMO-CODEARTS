package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Tenant;
import com.example.demo.entity.User;
import com.example.demo.mapper.TenantMapper;
import com.example.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tenant")
public class TenantController {
    
    @Autowired
    private TenantMapper tenantMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        
        Page<Tenant> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Tenant> wrapper = new LambdaQueryWrapper<>();
        
        if (keyword != null && !keyword.isEmpty()) {
            // 先查找匹配的用户ID
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.like(User::getUsername, keyword)
                      .or()
                      .like(User::getAccount, keyword);
            List<User> users = userMapper.selectList(userWrapper);
            
            if (!users.isEmpty()) {
                List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());
                wrapper.in(Tenant::getUserId, userIds);
            } else {
                // 没有匹配的用户，返回空结果
                Map<String, Object> response = new HashMap<>();
                response.put("records", List.of());
                response.put("total", 0);
                response.put("pages", 0);
                response.put("current", page);
                return ResponseEntity.ok(response);
            }
        }
        
        wrapper.orderByDesc(Tenant::getCreatedAt);
        Page<Tenant> result = tenantMapper.selectPage(pageParam, wrapper);
        
        // 填充用户信息
        List<Map<String, Object>> records = result.getRecords().stream().map(tenant -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", tenant.getId());
            item.put("userId", tenant.getUserId());
            item.put("checkInDate", tenant.getCheckInDate());
            item.put("rentAmount", tenant.getRentAmount());
            item.put("depositAmount", tenant.getDepositAmount());
            item.put("status", tenant.getStatus());
            item.put("createdAt", tenant.getCreatedAt());
            
            User user = userMapper.selectById(tenant.getUserId());
            if (user != null) {
                item.put("username", user.getUsername());
                item.put("account", user.getAccount());
            }
            
            return item;
        }).collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("records", records);
        response.put("total", result.getTotal());
        response.put("pages", result.getPages());
        response.put("current", result.getCurrent());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    public ResponseEntity<Tenant> create(@RequestBody Tenant tenant) {
        tenant.setStatus(1); // 默认在住
        tenantMapper.insert(tenant);
        return ResponseEntity.ok(tenant);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Tenant> update(@PathVariable Long id, @RequestBody Tenant tenant) {
        tenant.setId(id);
        tenantMapper.updateById(tenant);
        return ResponseEntity.ok(tenant);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        int rows = tenantMapper.deleteById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", rows > 0);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<Tenant>> getActiveTenants() {
        LambdaQueryWrapper<Tenant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tenant::getStatus, 1);
        return ResponseEntity.ok(tenantMapper.selectList(wrapper));
    }
}
