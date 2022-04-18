package com.example.springboota01.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.intern.InternUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.springboota01.common.Constants;
import com.example.springboota01.common.Result;
import com.example.springboota01.controller.dto.BlacklistDTO;
import com.example.springboota01.controller.dto.pageDTO.BlackPageDTO;
import com.example.springboota01.entity.Car;
import com.example.springboota01.mapper.BlacklistMapper;
import com.example.springboota01.mapper.CarMapper;
import com.example.springboota01.service.ICarService;
import io.swagger.annotations.*;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.springboota01.service.IBlacklistService;
import com.example.springboota01.entity.Blacklist;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author luoxu
 * @since 2022-03-13
 */
@Api(tags="blacklist类控制器：车辆黑名单管理")
@RestController
@RequestMapping("/blacklist")
@Transactional
public class BlacklistController {
    @Resource
    private IBlacklistService blacklistService;

    @Resource
    private BlacklistMapper blacklistMapper;

    @Resource
    private ICarService carService;

    // 新增或者更新
    @GetMapping("/save")
    @ApiOperation("实现添加黑名单功能,传递车牌号,需要车牌号有对应车辆")
    @ApiResponses({
            @ApiResponse(code = 200,message = "添加成功"),
            @ApiResponse(code = 400,message = "系统错误:没能成功保存记录"),
            @ApiResponse(code = 500,message = "没有添加黑名单的对应车辆或已经在黑名单中")
    })
    public Result save(@RequestParam String number) {
        QueryWrapper<Car> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("number",number);
        Car car = carService.getOne(queryWrapper);
        if(car == null){
           return Result.error(Constants.CODE_500,"没有对应车辆");
        }
        Blacklist blacklist = new Blacklist();
        blacklist = blacklistService.getOne(new QueryWrapper<Blacklist>().eq("car_id",car.getCarId()));
        if(blacklist!=null){
            return Result.error(Constants.CODE_500,"该车辆已经在黑名单中");
        }
        blacklist = new Blacklist();
        blacklist.setCarId(car.getCarId());
        if(blacklistService.save(blacklist)){
/*            car.setIllegalNum(car.getIllegalNum()+1);*/
            car.setBlacklist(true);
            carService.saveOrUpdate(car);
            return Result.success(Constants.CODE_200,"添加成功");
        }
        return Result.error(Constants.CODE_400,"系统错误");
    }

    // 根据id删除
    @GetMapping("/del/single")
    @ApiOperation("依据ID单个删除数据,对应的车辆标记为")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "单个id",required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 200,message = "删除成功"),
            @ApiResponse(code = 500,message = "删除失败")
    })
    public Result delete(@RequestParam Integer id) {
        Blacklist blist = blacklistService.getById(id);
        if(blacklistService.removeById(id) == true) {
            Integer carId = blist.getCarId();
            Car car = carService.getById(carId);
            car.setBlacklist(false);
/*            car.setIllegalNum(0);*/
            carService.saveOrUpdate(car);
            return Result.success(Constants.CODE_200,"删除成功");
        }
        return Result.error(Constants.CODE_500,"删除失败");
    }


    //分页查询
    @PostMapping("/page")
    @ApiOperation("获取页面尺寸和页码进行分页查询（可选带入车牌号进行模糊查询，同时给定起始时间与终止时间才会进行时间范围查询）")
    @ApiResponses({
            @ApiResponse(code = 200,message = "查询成功"),
            @ApiResponse(code = 400,message = "系统错误:页面与页面尺寸要大于0"),
            @ApiResponse(code = 500,message = "查询不到结果")
    })
    public Result findPage(@RequestBody BlackPageDTO bPage) {
        if(bPage.getPageNum()<1 || bPage.getPageSize()<1){
            return Result.error(Constants.CODE_400,"系统错误:页面或页面尺寸出错");
        }
        Integer pageNum = (bPage.getPageNum()-1) * bPage.getPageSize();
        bPage.setNumber("%" + bPage.getNumber() + "%");
        Map<String,Object> res = new HashMap<>();
        List<BlacklistDTO> data;
        Integer total;
        if(bPage.getBeginTime()!=null && bPage.getEndTime()!=null){
            data = blacklistMapper.selectTimePage(pageNum,bPage.getPageSize(),bPage.getNumber(),bPage.getBeginTime(),bPage.getEndTime());
            total = blacklistMapper.selectTimeTotal(bPage.getNumber(),bPage.getBeginTime(),bPage.getEndTime());
        }
        else{
            data = blacklistMapper.selectPage(pageNum,bPage.getPageSize(),bPage.getNumber());
            total = blacklistMapper.selectTotal(bPage.getNumber());
        }
        if(total == 0){
            return Result.error(Constants.CODE_500,"查询不到结果");
        }
        res.put("data",data);
        res.put("total",total);
        return Result.success(Constants.CODE_200,"查询成功",res);
    }



    /*    // 根据id批量删除
    @PostMapping("/del/batch")
    @ApiOperation("依据ID集合删除数据，实现批量删除的功能")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids",value = "id值的集合",dataType = "Integer",required = true)
    })
    public boolean deleteBatch(@RequestBody List<Integer> ids) {
        return blacklistService.removeByIds(ids);
    }*/
}


