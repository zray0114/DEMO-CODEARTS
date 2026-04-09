package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    
    public List<User> findAll() {
        return userMapper.selectList(null);
    }
    
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(userMapper.selectById(id));
    }
    
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(
            userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username)
            )
        );
    }
    
    public User save(User user) {
        if (user.getId() == null) {
            userMapper.insert(user);
        } else {
            userMapper.updateById(user);
        }
        return user;
    }
    
    public void deleteById(Long id) {
        userMapper.deleteById(id);
    }
    
    public boolean existsByUsername(String username) {
        return userMapper.selectCount(
            new LambdaQueryWrapper<User>().eq(User::getUsername, username)
        ) > 0;
    }
}
