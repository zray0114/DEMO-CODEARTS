package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.*;
import com.example.demo.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
@RequestMapping("/api/fee-share")
public class FeeShareController {
    
    @Autowired
    private FeeShareMapper feeShareMapper;
    
    @Autowired
    private TenantMapper tenantMapper;
    
    @Autowired
    private WaterFeeMapper waterFeeMapper;
    
    @Autowired
    private ElectricityFeeMapper electricityFeeMapper;
    
    @Autowired
    private GasFeeMapper gasFeeMapper;
    
    @Autowired
    private PropertyFeeMapper propertyFeeMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String month) {
        
        Page<FeeShare> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<FeeShare> wrapper = new LambdaQueryWrapper<>();
        
        if (month != null && !month.isEmpty()) {
            wrapper.eq(FeeShare::getShareMonth, month);
        }
        
        wrapper.orderByDesc(FeeShare::getShareMonth);
        Page<FeeShare> result = feeShareMapper.selectPage(pageParam, wrapper);
        
        // 为每个分摊记录添加租户姓名
        List<Map<String, Object>> records = new ArrayList<>();
        for (FeeShare share : result.getRecords()) {
            Map<String, Object> record = new HashMap<>();
            record.put("id", share.getId());
            record.put("tenantId", share.getTenantId());
            record.put("shareMonth", share.getShareMonth());
            record.put("waterFee", share.getWaterFee());
            record.put("electricityFee", share.getElectricityFee());
            record.put("gasFee", share.getGasFee());
            record.put("propertyFee", share.getPropertyFee());
            record.put("totalFee", share.getTotalFee());
            
            // 查询租户姓名
            Tenant tenant = tenantMapper.selectById(share.getTenantId());
            if (tenant != null) {
                User user = userMapper.selectById(tenant.getUserId());
                if (user != null) {
                    record.put("tenantName", user.getUsername());
                } else {
                    record.put("tenantName", "未知用户");
                }
            } else {
                record.put("tenantName", "未知租户");
            }
            
            records.add(record);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("records", records);
        response.put("total", result.getTotal());
        response.put("pages", result.getPages());
        response.put("current", result.getCurrent());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/calculate")
    public ResponseEntity<Map<String, Object>> calculate(
            @RequestParam String month,
            @RequestParam String method) {
        
        // 解析月份
        YearMonth yearMonth = YearMonth.parse(month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        int daysInMonth = yearMonth.lengthOfMonth();
        
        // 获取当月所有在住租户
        LambdaQueryWrapper<Tenant> tenantWrapper = new LambdaQueryWrapper<>();
        tenantWrapper.eq(Tenant::getStatus, 1);
        List<Tenant> tenants = tenantMapper.selectList(tenantWrapper);
        
        if (tenants.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "没有在住租户");
            return ResponseEntity.ok(result);
        }
        
        // 获取当月各项费用总额
        BigDecimal totalWater = getMonthTotalFee(waterFeeMapper, startDate, endDate);
        BigDecimal totalElectricity = getMonthTotalFee(electricityFeeMapper, startDate, endDate);
        BigDecimal totalGas = getMonthTotalFee(gasFeeMapper, startDate, endDate);
        BigDecimal totalProperty = getMonthTotalFee(propertyFeeMapper, startDate, endDate);
        
        // 删除该月已有的分摊记录
        LambdaQueryWrapper<FeeShare> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(FeeShare::getShareMonth, month);
        feeShareMapper.delete(deleteWrapper);
        
        // 计算每个租户的分摊金额
        List<FeeShare> shares = new ArrayList<>();
        
        if ("by_tenant".equals(method)) {
            // 按租户数量平均分摊
            int tenantCount = tenants.size();
            BigDecimal waterShare = totalWater.divide(BigDecimal.valueOf(tenantCount), 2, RoundingMode.HALF_UP);
            BigDecimal electricityShare = totalElectricity.divide(BigDecimal.valueOf(tenantCount), 2, RoundingMode.HALF_UP);
            BigDecimal gasShare = totalGas.divide(BigDecimal.valueOf(tenantCount), 2, RoundingMode.HALF_UP);
            BigDecimal propertyShare = totalProperty.divide(BigDecimal.valueOf(tenantCount), 2, RoundingMode.HALF_UP);
            
            for (Tenant tenant : tenants) {
                FeeShare share = new FeeShare();
                share.setTenantId(tenant.getId());
                share.setShareMonth(month);
                share.setWaterFee(waterShare);
                share.setElectricityFee(electricityShare);
                share.setGasFee(gasShare);
                share.setPropertyFee(propertyShare);
                share.setTotalFee(waterShare.add(electricityShare).add(gasShare).add(propertyShare));
                
                feeShareMapper.insert(share);
                shares.add(share);
            }
        } else if ("by_day".equals(method)) {
            // 按入住天数比例分摊
            // 计算总入住天数
            int totalDays = 0;
            Map<Long, Integer> tenantDays = new HashMap<>();
            
            for (Tenant tenant : tenants) {
                LocalDate checkIn = tenant.getCheckInDate();
                LocalDate effectiveStart = checkIn.isAfter(startDate) ? checkIn : startDate;
                
                long days = ChronoUnit.DAYS.between(effectiveStart, endDate) + 1;
                if (days > 0) {
                    tenantDays.put(tenant.getId(), (int) days);
                    totalDays += days;
                }
            }
            
            if (totalDays > 0) {
                for (Tenant tenant : tenants) {
                    Integer days = tenantDays.get(tenant.getId());
                    if (days == null || days == 0) continue;
                    
                    BigDecimal ratio = BigDecimal.valueOf(days).divide(BigDecimal.valueOf(totalDays), 6, RoundingMode.HALF_UP);
                    
                    FeeShare share = new FeeShare();
                    share.setTenantId(tenant.getId());
                    share.setShareMonth(month);
                    share.setWaterFee(totalWater.multiply(ratio).setScale(2, RoundingMode.HALF_UP));
                    share.setElectricityFee(totalElectricity.multiply(ratio).setScale(2, RoundingMode.HALF_UP));
                    share.setGasFee(totalGas.multiply(ratio).setScale(2, RoundingMode.HALF_UP));
                    share.setPropertyFee(totalProperty.multiply(ratio).setScale(2, RoundingMode.HALF_UP));
                    share.setTotalFee(share.getWaterFee().add(share.getElectricityFee())
                            .add(share.getGasFee()).add(share.getPropertyFee()));
                    
                    feeShareMapper.insert(share);
                    shares.add(share);
                }
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("shares", shares);
        result.put("totalWater", totalWater);
        result.put("totalElectricity", totalElectricity);
        result.put("totalGas", totalGas);
        result.put("totalProperty", totalProperty);
        
        return ResponseEntity.ok(result);
    }
    
    private BigDecimal getMonthTotalFee(Object mapper, LocalDate startDate, LocalDate endDate) {
        BigDecimal total = BigDecimal.ZERO;
        
        if (mapper instanceof WaterFeeMapper) {
            LambdaQueryWrapper<WaterFee> wrapper = new LambdaQueryWrapper<>();
            wrapper.between(WaterFee::getPaymentDate, startDate, endDate);
            List<WaterFee> fees = ((WaterFeeMapper) mapper).selectList(wrapper);
            total = fees.stream().map(WaterFee::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        } else if (mapper instanceof ElectricityFeeMapper) {
            LambdaQueryWrapper<ElectricityFee> wrapper = new LambdaQueryWrapper<>();
            wrapper.between(ElectricityFee::getPaymentDate, startDate, endDate);
            List<ElectricityFee> fees = ((ElectricityFeeMapper) mapper).selectList(wrapper);
            total = fees.stream().map(ElectricityFee::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        } else if (mapper instanceof GasFeeMapper) {
            LambdaQueryWrapper<GasFee> wrapper = new LambdaQueryWrapper<>();
            wrapper.between(GasFee::getPaymentDate, startDate, endDate);
            List<GasFee> fees = ((GasFeeMapper) mapper).selectList(wrapper);
            total = fees.stream().map(GasFee::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        } else if (mapper instanceof PropertyFeeMapper) {
            LambdaQueryWrapper<PropertyFee> wrapper = new LambdaQueryWrapper<>();
            wrapper.between(PropertyFee::getPaymentDate, startDate, endDate);
            List<PropertyFee> fees = ((PropertyFeeMapper) mapper).selectList(wrapper);
            total = fees.stream().map(PropertyFee::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        
        return total;
    }
}
