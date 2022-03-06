package com.example.springboota01.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.example.springboota01.service.IAdminService;
import com.example.springboota01.entity.Admin;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author luoxu
 * @since 2022-03-01
 */
@RestController
@RequestMapping("/admin")
        public class AdminController {
    @Resource
private IAdminService adminService;

    // 新增或者更新
    @PostMapping("/save")
    public boolean save(@RequestBody Admin admin) {
        return adminService.saveOrUpdate(admin);
    }
    // 根据id删除
    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Integer id) {
        return adminService.removeById(id);
    }
    // 根据id批量删除
    @PostMapping("/del/batch")
    public boolean deleteBatch(@RequestBody List<Integer> ids) {
        return adminService.removeByIds(ids);
    }
    // 查询所有
    @GetMapping
    public List<Admin> findAll() {
        return adminService.list();
    }
    // 根据id查找
    @GetMapping("/{id}")
    public Admin findOne(@PathVariable Integer id) {
        return adminService.getById(id);
    }
    //分页查询
    @GetMapping("/page")
    public Page<Admin> findPage(@RequestParam Integer pageNum,
                                    @RequestParam Integer pageSize) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        return adminService.page(new Page<>(pageNum, pageSize), queryWrapper);
    }
    }


