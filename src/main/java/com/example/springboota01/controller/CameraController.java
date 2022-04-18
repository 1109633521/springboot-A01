package com.example.springboota01.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboota01.mapper.CameraMapper;
import io.swagger.annotations.Api;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.example.springboota01.service.ICameraService;
import com.example.springboota01.entity.Camera;

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
@RequestMapping("/camera")
@Transactional
public class CameraController {
    @Resource
    private ICameraService cameraService;

    @Resource
    private CameraMapper cameraMapper;

}


