package com.example.springboota01.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboota01.common.Constants;
import com.example.springboota01.common.Result;
import com.example.springboota01.controller.vo.CrossCamera;
import com.example.springboota01.mapper.CrossMapper;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.example.springboota01.service.ICrossService;
import com.example.springboota01.entity.Cross;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author luoxu
 * @since 2022-03-11
 */

@RestController
@RequestMapping("/cross")
@Transactional
public class CrossController {
    @Resource
    private ICrossService crossService;

    @Resource
    private CrossMapper crossMapper;

    @GetMapping("/getAll")
    public Result getAllCross(){
        List<CrossCamera> crossCameras = crossMapper.getAllCross();
        if(crossCameras .size() == 0){
            return Result.error(Constants.CODE_500,"查询不到数据");
        }
        return Result.success(Constants.CODE_200,"查询成功",crossCameras);
    }
}


