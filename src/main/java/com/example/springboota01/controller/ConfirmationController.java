package com.example.springboota01.controller;


import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboota01.common.Constants;
import com.example.springboota01.common.Result;
import com.example.springboota01.controller.dto.BlacklistDTO;
import com.example.springboota01.controller.dto.CatchDTO;
import com.example.springboota01.controller.dto.ConfirmDTO;
import com.example.springboota01.controller.dto.IncidentDTO;
import com.example.springboota01.controller.dto.pageDTO.BlackPageDTO;
import com.example.springboota01.controller.dto.pageDTO.ConfirmPageDTO;
import com.example.springboota01.entity.*;
import com.example.springboota01.exception.ServiceException;
import com.example.springboota01.mapper.ConfirmationMapper;
import com.example.springboota01.mapper.IncidentMapper;
import com.example.springboota01.service.ICarService;
import com.example.springboota01.service.IIncidentService;
import com.example.springboota01.service.IUserService;
import io.swagger.annotations.*;
import lombok.Data;
import org.apache.tomcat.jni.Local;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.example.springboota01.service.IConfirmationService;

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
@RequestMapping("/confirmation")
@Api(tags="confirmation类控制器：事件管理")
public class ConfirmationController {
    @Resource
    private IConfirmationService confirmationService;

    @Resource
    private IUserService userService;

    @Resource
    private ConfirmationMapper confirmationMapper;

    @Resource
    private IIncidentService incidentService;

    @Resource
    private ICarService carService;

    // 根据id删除
    @ApiOperation("依据ID删除数据，实现单个删除的功能,删除对应的车辆的违章次数，若次数归0且在黑名单中，自动剔除")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id",value = "confirmation_id",required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 200,message = "删除成功"),
            @ApiResponse(code = 500,message = "删除失败")
    })
    @GetMapping("/del/single")
    public Result delete(@RequestParam Integer id) {
        Confirmation confirmation = confirmationService.getById(id);
        if(confirmationService.removeById(id) == true) {
            Integer incidentId = confirmation.getIncidentId();
            Incident incident = incidentService.getById(incidentId);
            Integer carId = incident.getCarId();
            if(incidentService.removeById(incidentId) == true) {
                Car car = carService.getById(carId);
                if (car.getIllegalNum() > 0) {
                    car.setIllegalNum(car.getIllegalNum() - 1);
                    carService.saveOrUpdate(car);
                    carService.blackCheck(carId);
                }
                return Result.success(Constants.CODE_200, "删除成功");
            }
        }
        return Result.error(Constants.CODE_500,"删除失败");
    }

    //分页查询
    @PostMapping("/page")
    @ApiOperation("获取页面尺寸和页码进行分页查询（可选择“是否确认”精准查询，同时给定起始时间与终止时间才会进行时间范围查询）")
    @ApiResponses({
            @ApiResponse(code = 200,message = "查询成功"),
            @ApiResponse(code = 400,message = "系统错误:页面与页面尺寸要大于0"),
            @ApiResponse(code = 500,message = "查询不到结果")
    })
    public Result findPage(@RequestBody ConfirmPageDTO page) {
        if(page.getPageNum()<1 || page.getPageSize()<1){
            return Result.error(Constants.CODE_400,"系统错误:页面或页面尺寸出错");
        }
        page.setPageNum((page.getPageNum()-1) * page.getPageSize());
        Map<String,Object> res = new HashMap<>();
        List<ConfirmDTO> data = confirmationMapper.findPage(page);;
        Integer total = confirmationMapper.findTotal(page);
        if(total == 0){
            return Result.error(Constants.CODE_500,"查询不到结果");
        }
        res.put("data",data);
        res.put("total",total);
        return Result.success(Constants.CODE_200,"查询成功",res);
    }

    @PostMapping("/update")
    @ApiOperation("修改事件信息")
    @ApiResponses({
            @ApiResponse(code = 200,message = "修改成功"),
            @ApiResponse(code = 500,message = "修改失败")
    })
    public Result findPage(@RequestBody changeConfirmDTO confirm,HttpServletRequest request){
        Confirmation confirmation = confirmationService.getById(confirm.getConfirmationId());
        Integer incidentId = confirmation.getIncidentId();
        Incident incident = incidentService.getById(incidentId);
        if(confirm.getIsConfirm()==true && confirmation.getIsConfirm()==false){
            String token = request.getHeader("token");
            // 获取 token 中的 user id
            String userId;
            userId = JWT.decode(token).getAudience().get(0);
            // 根据token中的userid查询数据库
            User user = userService.getById(userId);
            if (user == null) {
                throw new ServiceException(Constants.CODE_400, "系统错误");
            }
            confirmation.setUserName(user.getNickname());
            confirmation.setConfirmTime(LocalDateTime.now());
        }else if(confirm.getIsConfirm()==false && confirmation.getIsConfirm()==true){
            confirmation.setUserName(null);
            confirmation.setConfirmTime(null);
        }
        confirmation.setIsConfirm(confirm.getIsConfirm());
        if(confirm.getCreateTime()!=null){
            incident.setTime(confirm.getCreateTime());
            incidentService.updateById(incident);
        }

        confirmationService.updateById(confirmation);
        return Result.success(Constants.CODE_200,"修改成功");
    }
}

@Data
class changeConfirmDTO{
    private Integer confirmationId;
    private LocalDateTime createTime;
    private Boolean isConfirm;
}


