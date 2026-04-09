package com.example.demo.repository;

import com.example.demo.entity.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {
    List<LoginLog> findByUserIdOrderByLoginTimeDesc(Long userId);
    List<LoginLog> findByAccountOrderByLoginTimeDesc(String account);
}
