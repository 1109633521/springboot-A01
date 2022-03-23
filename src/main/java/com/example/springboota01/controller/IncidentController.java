package com.example.springboota01.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboota01.common.Constants;
import com.example.springboota01.common.Result;
import com.example.springboota01.controller.dto.IncidentDTO;
import com.example.springboota01.controller.dto.pageDTO.ConfirmPageDTO;
import com.example.springboota01.mapper.IncidentMapper;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.example.springboota01.service.IIncidentService;
import com.example.springboota01.entity.Incident;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author luoxu
 * @since 2022-03-19
 */
@RestController
@RequestMapping("/incident")
@Api(tags="incident控制类：违章事件管理")
public class IncidentController {
    @Resource
    private IIncidentService incidentService;

    @Resource
    private IncidentMapper incidentMapper;

    //分页查询
    @PostMapping("/latest")
    @ApiOperation("查询最新的五条违章记录")
    @ApiResponses({
            @ApiResponse(code = 200,message = "查询成功"),
            @ApiResponse(code = 500,message = "查询不到结果")
    })
    public Result findLatest() {
        Integer pageNum = 1;
        Integer pageSize = 5;
        List<IncidentDTO> data = incidentMapper.findLatest(pageNum,pageSize);
        Integer total = incidentMapper.getTotal();
        Map<String,Object> res = new HashMap<>();
        if(total == 0){
            return Result.error(Constants.CODE_500,"查询不到数据");
        }
        res.put("data",data);
        res.put("total",total);
        return Result.success(Constants.CODE_200,"查询成功",res);
    }
}


