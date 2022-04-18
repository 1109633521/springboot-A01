package com.example.springboota01.controller;


import com.auth0.jwt.JWT;
import com.example.springboota01.common.Constants;
import com.example.springboota01.common.Result;
import com.example.springboota01.controller.dto.*;
import com.example.springboota01.controller.dto.pageDTO.ConfirmPageDTO;
import com.example.springboota01.controller.vo.NewConfirm;
import com.example.springboota01.controller.vo.Counter;
import com.example.springboota01.controller.vo.VidInformation;
import com.example.springboota01.entity.*;
import com.example.springboota01.exception.ServiceException;
import com.example.springboota01.mapper.ConfirmationMapper;
import com.example.springboota01.mapper.CrossMapper;
import com.example.springboota01.service.*;
import io.swagger.annotations.*;
import lombok.Data;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

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
@Transactional
public class ConfirmationController {
    @Resource
    private IConfirmationService confirmationService;

    @Resource
    private IUserService userService;

    @Resource
    private ConfirmationMapper confirmationMapper;

    @Resource
    private CrossMapper crossMapper;

    @Resource
    private IIncidentService incidentService;

    @Resource
    private ICameraService cameraService;

    @Resource
    private ICarService carService;

    @Resource
    private ICrossService crossService;

    @Resource
    private ICatchService catchService;

    @Resource
    private IBlacklistService blacklistService;

    @Resource
    private IVideoService videoService;

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
    @ApiOperation("获取页面尺寸和页码进行分页查询（可选择“是否确认”精准查询，车牌号与违章类型模糊查询")
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
    @Transactional
    public Result update(@RequestBody changeConfirmDTO confirm,HttpServletRequest request){
        Confirmation confirmation = confirmationService.getById(confirm.getConfirmationId());
        Integer incidentId = confirmation.getIncidentId();
        Incident incident = incidentService.getById(incidentId);
            if (confirm.getIsConfirm() == true && confirmation.getIsConfirm() == false) {
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
            } else if (confirm.getIsConfirm() == false && confirmation.getIsConfirm() == true) {
                confirmation.setUserName(null);
                confirmation.setConfirmTime(null);
            }
            confirmation.setIsConfirm(confirm.getIsConfirm());
            if (confirm.getCreateTime() != null) {
                incident.setTime(confirm.getCreateTime());
                incidentService.updateById(incident);
            }
            confirmationService.updateById(confirmation);
        return Result.success(Constants.CODE_200,"修改成功");
    }

    @PostMapping("/insert")
    @ApiOperation("新增")
    @ApiResponses({
            @ApiResponse(code = 200,message = "修改成功"),
            @ApiResponse(code = 500,message = "修改失败")
    })
    @Transactional
    public Result insert(@RequestBody NewConfirm newConfirm){
        System.out.println(newConfirm);
        //Iterator<String> it =newConfirm.getOnline().keySet().iterator();
        TransData(newConfirm.getOverspeed(),newConfirm);
        TransData(newConfirm.getCar_break_red(),newConfirm);
        TransData(newConfirm.getOnline(),newConfirm);
        TransData(newConfirm.getPdestrian_blocked(),newConfirm);
        TransData(newConfirm.getReverse(),newConfirm);
        TransData(newConfirm.getWrong_way(),newConfirm);
        TransData(newConfirm.getTurn_round(),newConfirm);
        TransData(newConfirm.getIllegal_park(),newConfirm);
        return Result.success(Constants.CODE_200,"成功！");
    }

    @Transactional
    public Result TransData(Map<String,String[]> maps, NewConfirm newConfirm) {
        Iterator<String> it=maps.keySet().iterator();
        while(it.hasNext()) {
            String key = it.next();
            String[] strs = maps.get(key);
            System.out.println(strs);
            for (int i = 0; i < strs.length; i++) {
                System.out.println(strs[i]);
            }
            //插入新事件
            String res = insertFromFalsk(newConfirm.getCamera_id(), strs);
            if (res != "succeed") {
                return Result.error(Constants.CODE_500, "res");
            }
            Video video = videoService.getOne(new QueryWrapper<Video>()
                    .eq("camera_id", newConfirm.getCamera_id()));
            if (video == null) {
                return Result.error(Constants.CODE_400, "1");
            }
            Counter c = newConfirm.getCounter();
            VidInformation v = newConfirm.getVid_information();
            video.setData(c.getPerson(), c.getCar(), c.getMotorcycle(), c.getBus(), c.getTruck(), v.getFps(), v.getTotal_frames());
            System.out.println(video.getCar() + " " + video.getFps());
            videoService.updateById(video);
        }
        return Result.success();
    }

    //解析字符串,插入新的 事件，包括incident/confirmation/car/blacklist/catch
    @Transactional
    String insertFromFalsk(Integer cameraId, String[] strs){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime createTime = LocalDateTime.parse(strs[0], formatter);
        //锁定摄像机与路段
        String name = strs[1];
        String number = strs[2];
        if(number.substring(0,2)=="id"){
            //未查询出的车辆设置默认值
            number = "浙AJ1F30";
        }
        String type = strs[3];
        //fake车型数据
        String color = "黑色";
        String car_type = "轿车";
        Cross cross = crossMapper.getByName(name);
        /*Camera camera = cameraService.getOne(new QueryWrapper<Camera>()
                .eq("camera_number",cameraNumber).eq("cross_id",cross.getCrossId()));*/
        Camera camera = cameraService.getById(cameraId);
        if(camera==null){
            return "摄像头不存在";
        }
        //车辆管理
        Car car = carService.getOne(new QueryWrapper<Car>().eq("number",number));
        if(car == null){
            car = new Car();
            car.setNumber(number);
            car.setType(car_type);
            car.setColor(color);
            car.setIllegalNum(1);
            car.setBlacklist(true);
            car.setCreateTime(createTime);
            carService.save(car);
            car = carService.getOne(new QueryWrapper<Car>().eq("number",number));
        }else {
            car.setIllegalNum(car.getIllegalNum() + 1);
            carService.updateById(car);
        }
        //车辆捕获更新
        Catch bCatch = catchService.getOne(new QueryWrapper<Catch>().eq("car_id",car.getCarId()));
        if(bCatch !=null){
            bCatch.setCameraId(camera.getCameraId());
            bCatch.setCarId(car.getCarId());
            catchService.updateById(bCatch);
        }else {
            Catch aCatch = new Catch(camera.getCameraId(), car.getCarId(), createTime);
            catchService.save(aCatch);
        }
        //黑名单更新
        Blacklist blacklist = blacklistService.getOne(new QueryWrapper<Blacklist>().eq("car_id",car.getCarId()));
        if(blacklist == null) {
            Blacklist blackList = new Blacklist(car.getCarId(),LocalDateTime.now());
            blacklistService.save(blackList);
        }
        //新增事件
        Incident incident = new Incident(type,camera.getCameraId(),car.getCarId(),createTime);
        if(!incidentService.save(incident)){
            return "事件存储错误";
        }
        //新增确认记录
        incident = incidentService.getOne(new QueryWrapper<Incident>()
                .eq("type",type).eq("camera_id",camera.getCameraId())
                .eq("car_id",car.getCarId()).eq("time",createTime));
        Confirmation confirmation = new Confirmation(incident.getIncidentId());
        if(!confirmationService.save(confirmation)){
            return "确认事件存储错误";
        }
        return "succeed";
    }

    //八分化传递时间区间内的违章事件总数
    @GetMapping("/confirmCount")
    public Result confirmCount(@RequestParam String begin,
                               @RequestParam String end){
        //时间转换
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime beginTime = LocalDateTime.parse(begin, formatter);
        LocalDateTime endTime = LocalDateTime.parse(end, formatter);
        //获取事件总数
        Integer total = confirmationMapper.getTotal();
        //时间间隔获取
        Duration duration = Duration.between(beginTime, endTime);
        System.out.println(duration.getSeconds());
        Integer[] data = new Integer[8];
        LocalDateTime time1 = beginTime;
        LocalDateTime time2 = beginTime;
        for(int i=0;i<8;i++){
            time2 = time2.plusSeconds(duration.getSeconds()/8);
            System.out.println(time1.toString() + time2.toString());
            if(confirmationMapper.getCount(time1,time2)!=null) {
                data[i] = confirmationMapper.getCount(time1, time2);
            }else{
                data[i]=0;
            }
            time1 = time1.plusSeconds(duration.getSeconds()/8);
        }
        return Result.success(Constants.CODE_200,"成功",data);
    }
}

@Data
class changeConfirmDTO{
    private Integer confirmationId;
    private LocalDateTime createTime;
    private Boolean isConfirm;
}





