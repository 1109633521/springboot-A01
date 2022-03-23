package com.example.springboota01.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboota01.common.Constants;
import com.example.springboota01.common.Result;
import com.example.springboota01.controller.dto.pageDTO.CarPageDTO;
import com.example.springboota01.entity.User;
import com.example.springboota01.mapper.CarMapper;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * @since 2022-03-13
 */
@Api(tags="car类控制器：车辆管理")
@RestController
@RequestMapping("/car")
public class CarController {

    @Resource
    private ICarService carService;

    @Resource
    private CarMapper carMapper;

    /*// 新增或者更新
    @PostMapping("/save")
    public boolean save(@RequestBody Car car) {
        return carService.saveOrUpdate(car);
    }*/

    // 根据id删除
    /*@PostMapping("/del/signal")
    @ApiOperation("依据ID删除数据，实现单个删除的功能")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "单个id",required = true)
    })
    public Boolean delete(@PathVariable Integer id) {
        return carService.removeById(id);
    }*/

    // 根据id批量删除
    /*@PostMapping("/del/batch")
    @ApiOperation("依据ID集合删除数据，实现批量删除的功能")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids",value = "id值的集合",dataType = "Integer",required = true)
    })
    public boolean deleteBatch(@RequestBody List<Integer> ids) {
        return carService.removeByIds(ids);
    }*/

    //分页查询
    /*@GetMapping("/page")
    public Result findPage(@RequestBody CarPageDTO carPageDTO) {
        if(carPageDTO.getPageNum()==0 || carPageDTO.getPageSize()==0){
            return Result.error(Constants.CODE_400, "系统错误");
        }
        Integer pageNum = (carPageDTO.getPageNum() - 1)* carPageDTO.getPageSize();
        List<User> data = carMapper.selectPage(pageNum,carPageDTO.getPageSize());
        Integer total = carMapper.selectTotal();
        if(total == 0){
            return Result.error(Constants.CODE_500,"查询不到数据");
        }
        Map<String,Object> res = new HashMap<>();
        res.put("data",data);
        res.put("total",total);
        return Result.success(Constants.CODE_200,"查询成功",res);
    }*/

    @PostMapping("/getone")
    @ApiOperation("获取页面尺寸和页码以及车牌号进行精准查询,查询单个结果")
    @ApiResponses({
            @ApiResponse(code = 200,message = "查询成功"),
            @ApiResponse(code = 500,message = "查询不到数据")
    })
    public Result getOne(@RequestParam String number) {
        QueryWrapper<Car> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("number",number);
        //queryWrapper.like("username",username);  多条模糊查询，mp自动加 and
        Car one = carService.getOne(queryWrapper);
        if(one == null){
            return Result.error(Constants.CODE_500,"查询不到数据");
        }
        Map<String,Object> res = new HashMap<>();
        res.put("data",one);
        return Result.success(Constants.CODE_200,"查询成功",res);
    }

    @PostMapping("/page")
    @ApiOperation("获取页面尺寸和页码（车牌号、车型、颜色 多模糊动态查询）")
    @ApiResponses({
            @ApiResponse(code = 200,message = "查询成功"),
            @ApiResponse(code = 400,message = "系统错误:页码和页面尺寸为非0值"),
            @ApiResponse(code = 500,message = "查询不到数据")
    })
    public Result findPage(@RequestBody CarPageDTO cPage) {
        if(cPage.getPageNum()==0 || cPage.getPageSize()==0){
            return Result.error(Constants.CODE_400, "系统错误");
        }
        IPage<Car> page = new Page<>(cPage.getPageNum(),cPage.getPageSize());
        QueryWrapper<Car> queryWrapper = new QueryWrapper<>();
        if(cPage.getNumber()!=null) {queryWrapper.like("number",cPage.getNumber());}
        if(cPage.getType()!=null) {queryWrapper.like("type",cPage.getType());}
        if(cPage.getColor()!=null) {queryWrapper.like("color",cPage.getColor());}
        IPage<Car> data = carService.page(page,queryWrapper);
        Map<String,Object> res = new HashMap<>();
        if(data.getTotal() == 0){
            return Result.error(Constants.CODE_500,"查询不到数据");
        }
        res.put("data",data);
        return Result.success(Constants.CODE_200,"查询成功",res);
    }
}




