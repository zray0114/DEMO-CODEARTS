package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.PropertyFee;
import com.example.demo.mapper.PropertyFeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/property-fee")
public class PropertyFeeController {
    
    @Autowired
    private PropertyFeeMapper propertyFeeMapper;
    
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") String month) {
        
        Page<PropertyFee> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<PropertyFee> wrapper = new LambdaQueryWrapper<>();
        
        if (month != null && !month.isEmpty()) {
            LocalDate startDate = LocalDate.parse(month + "-01");
            LocalDate endDate = startDate.plusMonths(1).minusDays(1);
            wrapper.between(PropertyFee::getPaymentDate, startDate, endDate);
        }
        
        wrapper.orderByDesc(PropertyFee::getPaymentDate);
        Page<PropertyFee> result = propertyFeeMapper.selectPage(pageParam, wrapper);
        
        Map<String, Object> response = new HashMap<>();
        response.put("records", result.getRecords());
        response.put("total", result.getTotal());
        response.put("pages", result.getPages());
        response.put("current", result.getCurrent());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    public ResponseEntity<PropertyFee> create(
            @RequestParam("paymentDate") String paymentDate,
            @RequestParam("amount") BigDecimal amount,
            @RequestParam(value = "remark", required = false) String remark,
            @RequestParam(value = "images", required = false) MultipartFile[] images) throws IOException {
        
        PropertyFee waterFee = new PropertyFee();
        waterFee.setPaymentDate(LocalDate.parse(paymentDate));
        waterFee.setAmount(amount);
        waterFee.setRemark(remark);
        
        // 处理图片
        if (images != null && images.length > 0) {
            // 将多个图片合并为一个字节数组（简单实现）
            int totalSize = 0;
            for (MultipartFile file : images) {
                totalSize += (int) file.getSize();
            }
            
            byte[] allImages = new byte[totalSize];
            int offset = 0;
            for (MultipartFile file : images) {
                byte[] bytes = file.getBytes();
                System.arraycopy(bytes, 0, allImages, offset, bytes.length);
                offset += bytes.length;
            }
            
            waterFee.setImages(allImages);
        }
        
        propertyFeeMapper.insert(waterFee);
        return ResponseEntity.ok(waterFee);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PropertyFee> update(
            @PathVariable Long id,
            @RequestParam("paymentDate") String paymentDate,
            @RequestParam("amount") BigDecimal amount,
            @RequestParam(value = "remark", required = false) String remark,
            @RequestParam(value = "images", required = false) MultipartFile[] images) throws IOException {
        
        PropertyFee waterFee = new PropertyFee();
        waterFee.setId(id);
        waterFee.setPaymentDate(LocalDate.parse(paymentDate));
        waterFee.setAmount(amount);
        waterFee.setRemark(remark);
        
        // 处理图片
        if (images != null && images.length > 0) {
            int totalSize = 0;
            for (MultipartFile file : images) {
                totalSize += (int) file.getSize();
            }
            
            byte[] allImages = new byte[totalSize];
            int offset = 0;
            for (MultipartFile file : images) {
                byte[] bytes = file.getBytes();
                System.arraycopy(bytes, 0, allImages, offset, bytes.length);
                offset += bytes.length;
            }
            
            waterFee.setImages(allImages);
        }
        
        propertyFeeMapper.updateById(waterFee);
        return ResponseEntity.ok(waterFee);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        int rows = propertyFeeMapper.deleteById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", rows > 0);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/statistics")
    public ResponseEntity<List<Map<String, Object>>> statistics(
            @RequestParam @DateTimeFormat(pattern = "yyyy") Integer year) {
        
        LambdaQueryWrapper<PropertyFee> wrapper = new LambdaQueryWrapper<>();
        wrapper.apply("YEAR(payment_date) = {0}", year);
        List<PropertyFee> fees = propertyFeeMapper.selectList(wrapper);
        
        // 按月份统计
        Map<Integer, Double> monthlyStats = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
            monthlyStats.put(i, 0.0);
        }
        
        for (PropertyFee fee : fees) {
            int month = fee.getPaymentDate().getMonthValue();
            monthlyStats.put(month, monthlyStats.get(month) + fee.getAmount().doubleValue());
        }
        
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (Map.Entry<Integer, Double> entry : monthlyStats.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("month", entry.getKey());
            item.put("amount", entry.getValue());
            result.add(item);
        }
        
        return ResponseEntity.ok(result);
    }
}
