package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.entity.Menu;
import com.example.demo.mapper.MenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
@CrossOrigin(origins = "http://localhost:5173")
public class MenuController {

    @Autowired
    private MenuMapper menuMapper;

    @GetMapping("/list")
    public ResponseEntity<List<Menu>> list() {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Menu::getSortOrder);
        List<Menu> menus = menuMapper.selectList(wrapper);
        return ResponseEntity.ok(menus);
    }

    @PostMapping
    public ResponseEntity<Menu> create(@RequestBody Menu menu) {
        menuMapper.insert(menu);
        return ResponseEntity.ok(menu);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Menu> update(@PathVariable Long id, @RequestBody Menu menu) {
        menu.setId(id);
        menuMapper.updateById(menu);
        return ResponseEntity.ok(menu);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        menuMapper.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
