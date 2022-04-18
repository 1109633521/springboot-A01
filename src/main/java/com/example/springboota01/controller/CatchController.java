package com.example.springboota01.controller;


import com.example.springboota01.common.Constants;
import com.example.springboota01.common.Result;
import com.example.springboota01.controller.vo.CatchVO;
import com.example.springboota01.mapper.CatchMapper;
import io.swagger.annotations.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.springboota01.service.ICatchService;

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
@RequestMapping("/catch")
@Api(tags="catch类控制器：摄像头捕获管理")
@Transactional
public class CatchController {
    @Resource
    private ICatchService catchService;

    @Resource
    private CatchMapper catchMapper;

    // 新增数据
    @PostMapping("/latest")
    @ApiOperation("获取最新的四条车辆记录-车辆最新记录")
    @ApiResponses({
            @ApiResponse(code = 200,message = "查询成功"),
            @ApiResponse(code = 500,message = "查询不到数据")
    })
    public Result findLastest() {
        Integer pageNum = 0;
        Integer pageSize = 4;
        List<CatchVO> data = catchMapper.selectPage(pageNum,pageSize);
        Integer total = catchMapper.getTotal();
        Map<String,Object> res = new HashMap<>();
        res.put("data",data);
        res.put("total",total);
        if(total == 0){
            return Result.error(Constants.CODE_500,"查询不到数据");
        }
        return Result.success(Constants.CODE_200,"查询成功",res);
    }
}


