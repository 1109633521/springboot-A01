package com.example.springboota01.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.example.springboota01.service.ICarService;
import com.example.springboota01.entity.Car;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author luoxu
 * @since 2022-03-18
 */
@RestController
@RequestMapping("/car")
        public class CarController {
        @Resource
    private ICarService carService;

    @Resource
    private CarMapper carMapper;

    // 新增数据
    @PostMapping("/save")
    public boolean save(@RequestBody Car car) {
        return carService.save(car);
    }

    // 根据id删除
    @ApiOperation("依据ID删除数据，实现单个删除的功能")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id",value = "单个id",required = true)
    })
    @DeleteMapping("/del/signal")
    public Boolean delete(@PathVariable Integer id) {
        return carService.removeById(id);
    }

    // 根据id批量删除
    @PostMapping("/del/batch")
    @ApiOperation("依据ID集合删除数据，实现批量删除的功能")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids",value = "id值的集合",dataType = "Integer",required = true)
    })
    public boolean deleteBatch(@RequestBody List<Integer> ids) {
        return carService.removeByIds(ids);
    }

    //分页查询
    @PostMapping("/page")
    @ApiOperation("获取页面尺寸和页码进行分页查询")
    @ApiResponses({
            @ApiResponse(code = 200,message = "查询成功"),
            @ApiResponse(code = 400,message = "系统错误:"),
            @ApiResponse(code = 500,message = "查询不到数据")
    })
    public Result findPage(@RequestBody CarPageDTO carPage) {
        
    }
    }


