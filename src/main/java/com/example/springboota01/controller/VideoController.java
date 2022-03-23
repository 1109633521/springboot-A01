package com.example.springboota01.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.assist.ISqlRunner;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.springboota01.common.Constants;
import com.example.springboota01.common.Result;
import com.example.springboota01.controller.dto.VideoDTO;
import com.example.springboota01.controller.dto.pageDTO.VideoPageDTO;
import com.example.springboota01.entity.*;
import com.example.springboota01.mapper.CameraMapper;
import com.example.springboota01.mapper.CrossMapper;
import com.example.springboota01.mapper.FilesMapper;
import com.example.springboota01.mapper.VideoMapper;
import com.example.springboota01.service.ICameraService;
import com.example.springboota01.service.IFilesService;
import com.example.springboota01.service.impl.CrossServiceImpl;
import com.example.springboota01.utils.VideoUtil;
import io.swagger.annotations.*;
import lombok.Data;
import org.apache.tomcat.jni.Local;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.springboota01.service.IVideoService;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author luoxu
 * @since 2022-03-11
 */
@Api(tags="video类控制器：路段管理")
@RestController
@RequestMapping("/video")
public class VideoController {
    @Value("${files.upload.path}")
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
    private FilesMapper filesMapper;

    // 根据id删除
    @ApiOperation("依据ID删除数据，实现单个删除的功能")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "video_id", required = true)
    })
    @GetMapping("/del/single")
    public Result delete(@RequestParam Integer id) {
        Video video = videoMapper.selectById(id);
        if(video == null){
            return Result.error(Constants.CODE_500,"没有对应id的视频资源，删除失败");
        }
        Files files = filesMapper.selectOne(new QueryWrapper<Files>().eq("url",video.getUrl()));
        if(files == null){
            videoMapper.deleteById(video);
            return Result.success(Constants.CODE_200,"视频资源对应文件已不存在，video已删除");
        }
        if(delFile(video.getUrl()) == false){
            return Result.error(Constants.CODE_400,"系统错误，没有找到资源文件");
        }
        cameraMapper.deleteById(video.getCameraId());
        videoMapper.deleteById(video);
        filesMapper.deleteById(files);
        return Result.success(Constants.CODE_200,"删除成功");
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
            delFile(i.getUrl());
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

    @GetMapping("/play/{fileUUID}")
    @ApiOperation("进行视频播放")
    public Result playVideo(@PathVariable String fileUUID, HttpServletResponse response) throws IOException {
        // 根据文件的唯一标识码获取文件
        File file = new File(fileUploadPath + fileUUID);
        try {
            // 设置输出流的格式
            FileInputStream inputStream = new FileInputStream(file);
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            String diskfilename = "final.mp4";
            response.setContentType("video/mp4");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + diskfilename + "\"");
            System.out.println("data.length " + data.length);
            response.setContentLength(data.length);
            response.setHeader("Content-Range", "" + Integer.valueOf(data.length - 1));
            response.setHeader("Accept-Ranges", "bytes");
            response.setHeader("Etag", "W/\"9767057-1323779115364\"");
            OutputStream os = response.getOutputStream();
            os.write(data);
            //先声明的流后关掉！
            os.flush();
            os.close();
            inputStream.close();
        } catch (Exception e) {

        }
        return Result.success(Constants.CODE_200, "播放成功");
    }

    @PostMapping("/getCameraId")
    @ApiOperation("获取路段以及摄像头编号对应 摄像头id")
    @ApiResponses({
            @ApiResponse(code = 200,message = "获取成功"),
            @ApiResponse(code = 500,message = "没有对应路段 或 没有对应摄像头")
    })
    public Result getCameraId(@RequestParam String name,
                              @RequestParam Integer cameraNumber){
        QueryWrapper<Cross> queryCross = new QueryWrapper<>();
        queryCross.eq("name",name);
        Cross cross = crossMapper.selectOne(queryCross);
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

    @PostMapping("/upload")
    @ApiOperation("保存视频文件，生成video类视频信息与文件信息，需要传入路段名和视频文件")
    @ApiResponses({
            @ApiResponse(code = 200,message = "存储成功"),
            @ApiResponse(code = 400,message = "存储失败，系统错误"),
            @ApiResponse(code = 500,message = "没有对应路段")
    })
    public Result upload(@RequestParam MultipartFile file,
                         @RequestParam String name){
        //Cross cross = crossMapper.selectOne(new QueryWrapper<Cross>().eq("name",name));
        Cross cross = crossMapper.getByName(name);
        if(cross == null){
            return Result.error(Constants.CODE_500,"没有对应路段");
        }
        //保存文件获取url
        String url = new String();
        try {
            url = filesService.upload(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(url == null){
            Result.error(Constants.CODE_400,"存储失败，系统错误");
        }
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("url",url);
        Files files = filesMapper.selectOne(queryWrapper);
        //生成新摄像头并获取id
        Integer number = cameraMapper.getNumber(cross.getCrossId());
        if(number == null){
            number = 0;
        }
        Camera camera = new Camera(number + 1 ,cross.getCrossId());
        cameraMapper.insert(camera);
        Integer cId = (cameraMapper.selectOne(new QueryWrapper<Camera>()
                .eq("camera_number",camera.getCameraNumber())
        .eq("cross_id",camera.getCrossId()))).getCameraId();
        //设置video属性
        Video video = new Video();
        video.setLength(getTime(file));
        video.setCameraId(cId);
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

    //获取视频时间
    private LocalTime getTime(MultipartFile file){
        String duration = VideoUtil.ReadVideoTimeMs(file);
        String[] str = duration.split(":");
        return LocalTime.of(Integer.parseInt(str[0]),
                Integer.parseInt(str[1]),
                Integer.parseInt(str[2]));
    }

    private boolean delFile(String url){
        File delFile = new File(fileUploadPath+url);
        if(delFile.isFile() && delFile.exists()) {
            delFile.delete();
            return true;
        }else {
            return false;
        }
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
