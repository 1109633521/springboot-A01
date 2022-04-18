package com.example.springboota01.controller;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.springboota01.common.Constants;
import com.example.springboota01.common.Result;
import com.example.springboota01.controller.dto.VideoDTO;
import com.example.springboota01.controller.dto.pageDTO.VideoPageDTO;
import com.example.springboota01.controller.vo.CarAndPerson;
import com.example.springboota01.controller.vo.CarTypeData;
import com.example.springboota01.controller.vo.Counter;
import com.example.springboota01.controller.vo.CrossData;
import com.example.springboota01.entity.*;
import com.example.springboota01.mapper.*;
import com.example.springboota01.service.ICameraService;
import com.example.springboota01.service.IFilesService;
import com.example.springboota01.utils.VideoUtil;
import io.swagger.annotations.*;
import lombok.Data;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.example.springboota01.service.IVideoService;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author luoxu
 * @since 2022-03-11
 */
enum IncidentType
{
    wu,overspeed, car_break_red, online, pedestrian_red, reverse, pdestrian_blocked, wrong_way, turn_round, illegal_park;
}
@Api(tags="video类控制器：路段管理")
@RestController
@RequestMapping("/video")
@Transactional
public class VideoController {
    @Value("${files.upload.path_video}")
    private String fileUploadPath;

    @Resource
    private IVideoService videoService;

    @Resource
    private IFilesService filesService;

    @Resource
    private ICameraService cameraService;

    @Resource
    private VideoMapper videoMapper;

    @Resource
    private CameraMapper cameraMapper;

    @Resource
    private CrossMapper crossMapper;

    @Resource
    private CarMapper carMapper;

    @Resource
    private FilesMapper filesMapper;

    // 根据id删除
    @ApiOperation("依据ID删除数据，实现单个删除的功能")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "video_id", required = true)
    })
    @GetMapping("/del/single")
    public Result delete(@RequestParam Integer id) {
        Video video = videoMapper.selectById(id);
        if (video == null) {
            return Result.error(Constants.CODE_500, "没有对应id的视频资源，删除失败");
        }
        Files files = filesMapper.selectOne(new QueryWrapper<Files>().eq("url", video.getUrl()));
        if (files == null) {
            videoMapper.deleteById(video);
            return Result.success(Constants.CODE_200, "视频资源对应文件已不存在，video已删除");
        }
        if (filesService.delFile(video.getUrl(),fileUploadPath) == false) {
            return Result.error(Constants.CODE_400, "系统错误，没有找到资源文件");
        }
        videoMapper.deleteById(video);
        filesMapper.deleteById(files);
        return Result.success(Constants.CODE_200, "删除成功");
    }

    // 根据id批量删除
    @ApiOperation("依据ID删除数据，实现批量删除的功能")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "video_id", required = true)
    })
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        List<Video> videoList = videoMapper.selectBatchIds(ids);
        List<Integer> fileIds = new ArrayList<>();
        List<Integer> cameraIds = new ArrayList<>();
        for (Video i:videoList) {
            QueryWrapper<Files> queryFiles = new QueryWrapper<>();
            queryFiles.eq("url",i.getUrl());
            Files files = filesMapper.selectOne(queryFiles);
            fileIds.add(files.getId());
            cameraIds.add(i.getCameraId());
        }
        List<Files> filesList = filesMapper.selectBatchIds(fileIds);
        for(Files i:filesList){
            filesService.delFile(i.getUrl(),fileUploadPath);
        }
        if (videoService.removeByIds(ids) == true
                && filesService.removeByIds(fileIds)
                && cameraService.removeByIds(cameraIds)) {
            return Result.success("删除成功");
        }
        return Result.error(Constants.CODE_400, "删除失败");
    }

    //分页查询
    @PostMapping("/page")
    @ApiOperation("页码、页面尺寸、路段名：分页查询    路段名：总体查询")
    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功"),
            @ApiResponse(code = 400, message = "系统错误:页面与页面尺寸要大于0"),
            @ApiResponse(code = 500, message = "查询不到结果")
    })
    public Result findPage(@RequestBody VideoPageDTO page) {
        Integer total = videoMapper.findTotal(page);
        List<VideoDTO> data;
        if (total == 0) {
            return Result.error(Constants.CODE_500, "查询不到结果");
        }
        //判断是否需要分页查询
        if (page.getPageNum() == null && page.getPageSize() == null) {
            page.setPageNum(1);
            page.setPageSize(total);
            data = videoMapper.findAll(page.getName());
        } else if (page.getPageNum() < 1 || page.getPageSize() < 1) {
            return Result.error(Constants.CODE_400, "系统错误:页面或页面尺寸出错");
        } else {
            page.setPageNum((page.getPageNum() - 1) * (page.getPageSize()));
            data = videoMapper.findPage(page);
        }
        //数据包装
        Map<String, Object> res = new HashMap<>();
        res.put("data", data);
        res.put("total", total);
        return Result.success(Constants.CODE_200, "查询成功", res);
    }

    @GetMapping("/show/{fileUUID}")
    @ApiOperation("进行视频播放")
    public void showVideo(@PathVariable String fileUUID, HttpServletResponse response) throws IOException {
        filesService.showVideo(fileUUID,response,fileUploadPath);
    }

    @GetMapping("/getCameraId")
    @ApiOperation("获取路段以及摄像头编号对应 摄像头id")
    @ApiResponses({
            @ApiResponse(code = 200,message = "获取成功"),
            @ApiResponse(code = 500,message = "没有对应路段 或 没有对应摄像头")
    })
    public Result getCameraId(@RequestParam String name,
                              @RequestParam Integer cameraNumber){
        Cross cross = crossMapper.getByName(name);
        if(cross == null){
            return Result.error(Constants.CODE_500,"没有对应路段");
        }
        QueryWrapper<Camera> queryCamera = new QueryWrapper<>();
        queryCamera.eq("cross_id",cross.getCrossId());
        queryCamera.eq("camera_number",cameraNumber);
        Camera camera = cameraMapper.selectOne(queryCamera);
        if(camera == null){
            return Result.error(Constants.CODE_500,"没有对应摄像头");
        }
        else{
            return Result.success(Constants.CODE_500,"获取成功",camera.getCameraId());
        }
    }

    @GetMapping("/getUrl")
    @ApiOperation("获取路段以及摄像头编号对应 摄像头id")
    @ApiResponses({
            @ApiResponse(code = 200,message = "获取成功"),
            @ApiResponse(code = 500,message = "没有对应路段 或 没有对应摄像头")
    })
    public Result getUrl(@RequestParam String name,
                              @RequestParam Integer cameraNumber){
        Cross cross = crossMapper.getByName(name);
        if(cross == null){
            return Result.error(Constants.CODE_500,"没有对应路段");
        }
        QueryWrapper<Camera> queryCamera = new QueryWrapper<>();
        queryCamera.eq("cross_id",cross.getCrossId());
        queryCamera.eq("camera_number",cameraNumber);
        Camera camera = cameraMapper.selectOne(queryCamera);
        if(camera == null){
            return Result.error(Constants.CODE_500,"没有对应摄像头");
        }
        Video video = videoService.getOne(new QueryWrapper<Video>().eq("camera_id",camera.getCameraId()));
        if(video == null){
            return Result.error(Constants.CODE_500,"没有对应视频数据");
        }
        else{
            return Result.success(Constants.CODE_200,"获取成功",video.getUrl());
        }
    }

    @PostMapping("/upload")
    @Transactional
    public Result upload(@RequestParam MultipartFile file,
                         @RequestParam Integer cameraId){
        Video oldVideo = videoService.getOne(new QueryWrapper<Video>().eq("camera_id",cameraId));
        if(oldVideo != null){
            delete(oldVideo.getVideoId());
        }
        //保存文件获取url
        String url = new String();
        try {
            url = filesService.uploadVideo(file,fileUploadPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(url == null){
            Result.error(Constants.CODE_400,"存储失败，系统错误");
        }
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("url",url);
        Files files = filesMapper.selectOne(queryWrapper);
        //获取摄像头id
        if(cameraService.getById(cameraId)==null){
            return Result.error(Constants.CODE_400,"不存在摄像头");
        }
        //设置video属性
        Video video = new Video();
        video.setLength(getTime(file));
        video.setCameraId(cameraId);
        video.setUrl(files.getUrl());
        videoService.save(video);
        return Result.success(Constants.CODE_200,"存储成功",url);
    }

    @GetMapping("/download/{fileUUID}")
    @ApiOperation("文件下载")
    public void download(@PathVariable String fileUUID, HttpServletResponse response) throws IOException {
        // 根据文件的唯一标识码获取文件
        File uploadFile = new File(fileUploadPath + fileUUID);
        // 设置输出流的格式
        ServletOutputStream os = response.getOutputStream();
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileUUID, "UTF-8"));
        response.setContentType("application/octet-stream");
        // 读取文件的字节流
        os.write(FileUtil.readBytes(uploadFile));
        os.flush();
        os.close();
    }

    //查询所有
    @GetMapping("/crossList")
    @ApiOperation("获取全部路段信息列表")
    private Result findAll(@RequestParam String name){
        //获取路段列表
        List<Cross> crossList;
        Integer total = videoMapper.findTotal(new VideoPageDTO(name));
        if(name != null) {
            crossList = crossMapper.getGroup(name);
        }else{
            crossList = crossMapper.getGroup(null);
        }
        if (total == 0) {
            return Result.error(Constants.CODE_500, "查询不到结果");
        }
        Map<String, Object> res = new HashMap<>();
        res.put("total", total);
        //封装数据
        Map<String, Object> resData = new HashMap<>();
        for(Cross cross:crossList){
            resData.put(cross.getName(),new CrossGroup(videoMapper.findAll(cross.getName())
                    ,cross.getName(),videoMapper.findTotal(new VideoPageDTO(cross.getName()))));
        }
        res.put("roadlist", resData);
        return Result.success(Constants.CODE_200, "查询成功", res);
    }


    @ApiOperation("获取一条视频流量统计信息")
    @GetMapping("/data")
    public Result videoData(@RequestParam Integer cameraId){
        Video video = videoService.getOne(new QueryWrapper<Video>().eq("camera_id",cameraId));
        if(video == null){
            return Result.error(Constants.CODE_400,"查询不到数据");
        }
        return Result.success(Constants.CODE_200,"查询成功",video);
    }

    @ApiOperation("获取路段流量统计信息")
    @GetMapping("/crossData")
    public Result crossData(@RequestParam String name,
                            @RequestParam String begin,
                            @RequestParam String end){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime beginTime = LocalDateTime.parse(begin, formatter);
        LocalDateTime endTime = LocalDateTime.parse(end,formatter);
        CrossData crossData = videoMapper.getCrossData(name,beginTime,endTime);
        if(crossData == null){
            return Result.error(Constants.CODE_400,"查询不到数据");
        }
        return Result.success(Constants.CODE_200,"查询成功",crossData);
    }

    @ApiOperation("获取交通工具流量变化趋势信息信息")
    @GetMapping("/crossFlowData")
    public Result crossFlowData(@RequestParam String name,
                            @RequestParam String begin,
                            @RequestParam String end){
        /*if (name == null){
            name = "";
        }*/
        LocalDateTime beginTime;
        LocalDateTime endTime;
        if(end == ""||begin==""){
            endTime = LocalDateTime.now();
            beginTime = endTime.minusDays(7);
        }else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            beginTime = LocalDateTime.parse(begin, formatter);
            endTime = LocalDateTime.parse(end, formatter);
        }
        //时间间隔获取
        Duration duration = Duration.between(beginTime, endTime);
        System.out.println(duration.getSeconds());
        List<Counter> data1 = new ArrayList<>();
        List<CarAndPerson> data2 = new ArrayList<>();
        LocalDateTime time1 = beginTime;
        LocalDateTime time2 = beginTime;
        for(int i=0;i<7;i++){
            time2 = time2.plusSeconds(duration.getSeconds()/7);
            System.out.println(time1.toString() + time2.toString());
            if(videoMapper.getFlowData(name,time1,time2)!=null) {
                data1.add(videoMapper.getFlowData(name, time1, time2));
            }else{
                data1.add(new Counter(0,0,0,0,0));
            }
            data2.add(new CarAndPerson(data1.get(i).getBus()+data1.get(i).getCar()
                    +data1.get(i).getMotorcycle()+data1.get(i).getTruck(),data1.get(i).getPerson()));
            time1 = time1.plusSeconds(duration.getSeconds()/7);
        }
        Map<String,Object> res = new HashMap<>();
        res.put("data1",data1);
        res.put("data2",data2);
        return Result.success(Constants.CODE_200,"查询成功",res);
    }

    @ApiOperation("获取路段最后一段视频url")
    @GetMapping("/crossVideo")
    public Result getCrossVideo(@RequestParam Integer crossId){
        String url = videoMapper.getCrossVideo(crossId);
        if(url == null){
            return Result.error(Constants.CODE_400,"查询不到数据");
        }
        return Result.success(Constants.CODE_200,"查询成功",url);
    }

    @GetMapping("/carTypeData")
    @ApiOperation("首页车型统计信息")
    public Result carTypeData(Integer cameraId){
        List<CarTypeData> carTypeData = new ArrayList<>();
        Counter counter = videoMapper.getCarTypeData(cameraId);
        if(counter == null){
            carTypeData.add(new CarTypeData(1,"truck",0));
            carTypeData.add(new CarTypeData(3,"car",0));
            carTypeData.add(new CarTypeData(4,"bus",0));
            carTypeData.add(new CarTypeData(5,"motorcycle",0));
        }else{
            carTypeData.add(new CarTypeData("car",counter.getCar()));
            carTypeData.add(new CarTypeData("truck",counter.getTruck()));
            carTypeData.add(new CarTypeData("bus",counter.getBus()));
            carTypeData.add(new CarTypeData("motorcycle",counter.getMotorcycle()));
            carTypeData.sort(Comparator.comparing(CarTypeData::getCount).reversed());
            for(int i=0;i<carTypeData.size();i++){
                carTypeData.get(i).setId(i+1);
            }
        }
        return Result.success(Constants.CODE_200,"查询成功",carTypeData);
    }

    //获取视频时间
    private LocalTime getTime(MultipartFile file){
        String duration = VideoUtil.ReadVideoTimeMs(file);
        String[] str = duration.split(":");
        return LocalTime.of(Integer.parseInt(str[0]),
                Integer.parseInt(str[1]),
                Integer.parseInt(str[2]));
    }
}

@Data
class CrossGroup{
    private Integer total;
    private String name;
    private List<VideoDTO> cameraList;
    public CrossGroup(List<VideoDTO> cameraList ,String name,Integer total) {
        this.total = total;
        this.name = name;
        this.cameraList = cameraList;
    }
}

