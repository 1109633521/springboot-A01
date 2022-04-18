package com.example.springboota01.controller;


import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.mail.MailUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboota01.common.Constants;
import com.example.springboota01.common.Result;
import com.example.springboota01.entity.*;
import com.example.springboota01.mapper.ConfirmationMapper;
import com.example.springboota01.mapper.FilesMapper;
import com.example.springboota01.mapper.ImageMapper;
import com.example.springboota01.mapper.VideoMapper;
import com.example.springboota01.service.*;
import io.swagger.annotations.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author luoxu
 * @since 2022-03-25
 */
@RestController
@RequestMapping("/image")
@Transactional(rollbackFor=Exception.class)
@Api(tags="image类控制器：违章图片管理")
public class ImageController {
    @Value("${files.upload.path_image}")
    private String fileUploadPath;

    @Resource
    private ImageMapper imageMapper;
    @Resource
    private VideoMapper videoMapper;
    @Resource
    private IVideoService videoService;
    @Resource
    private IImageService imageService;
    @Resource
    private IConfirmationService confirmationService;
    @Resource
    private IIncidentService incidentService;
    @Resource
    private ConfirmationMapper confirmationMapper;
    @Resource
    private IFilesService filesService;
    @Resource
    private ICarService carService;
    @Resource
    private IFragmentService fragmentService;
    @Resource
    private FilesMapper filesMapper;

    @PostMapping("/upload")
    @ApiOperation("获取路段以及摄像头编号对应 摄像头id")
    @ApiResponses({
            @ApiResponse(code = 200,message = "获取成功"),
            @ApiResponse(code = 500,message = "对应视频与事件不存在")
    })
    public Result upload(@RequestParam Integer confirmationId,
                         @RequestParam MultipartFile file){
        Confirmation confirmation = confirmationService.getById(confirmationId);
        if(confirmation == null){
            return Result.error(Constants.CODE_500,"对应视频与事件不存在");
        }
        //保存文件获取url
        String url = new String();
        try {
            url = filesService.upload(file,fileUploadPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Image image = new Image (confirmationId,url);
        imageService.save(image);
        Image image02 = imageService.getById(image.getImageId());
        return Result.success(Constants.CODE_200,"保存成功",image02);
    }

    @GetMapping("/show/{fileUUID}")
    @ApiOperation("图片展示")
    public void download(@PathVariable String fileUUID, HttpServletResponse response) throws IOException {
        filesService.showImage(fileUUID,response,fileUploadPath);
    }

    @GetMapping("/getImageUrl")
    @ApiOperation("输入confirmation事件id获得图片Url")
    public Result showImageWithConfirm(@RequestParam Integer confirmationId) throws IOException {
        Confirmation confirmation = confirmationMapper.selectById(confirmationId);
        if(confirmation == null){ return Result.error(Constants.CODE_500,"不存在对应事件");}
        List<Image> images = imageMapper.selectList(new QueryWrapper<Image>().eq("confirmation_id",confirmationId));
        if(images.size() == 0){ return Result.error(Constants.CODE_500,"不存在对应图片");}
        return Result.success(Constants.CODE_200,"获取成功",images);
    }

    @ApiOperation("图片单个删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "video_id", required = true)
    })
    @GetMapping("/del/single")
    public Result delete(@RequestParam Integer id) {
        Image image = imageMapper.selectById(id);
        if (image == null) {
            return Result.error(Constants.CODE_500, "没有对应id的图片资源，删除失败");
        }
        Files files = filesService.getOne(new QueryWrapper<Files>().eq("url", image.getUrl()));
        if (files == null) {
            imageMapper.deleteById(image);
            return Result.success(Constants.CODE_200, "图片资源对应文件已不存在，image已删除");
        }
        if (filesService.delFile(image.getUrl(),fileUploadPath) == false) {
            return Result.error(Constants.CODE_400, "系统错误，没有找到资源文件");
        }
        imageMapper.deleteById(image.getImageId());
        filesMapper.deleteById(files);
        return Result.success(Constants.CODE_200, "删除成功");
    }

    // 根据id批量删除
    @ApiOperation("批量删除图片")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "video_id", required = true)
    })
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        List<Image> imageList = imageMapper.selectBatchIds(ids);
        List<Integer> fileIds = new ArrayList<>();
        for (Image i:imageList) {
            QueryWrapper<Files> queryFiles = new QueryWrapper<>();
            queryFiles.eq("url",i.getUrl());
            Files files = filesMapper.selectOne(queryFiles);
            fileIds.add(files.getId());
        }
        List<Files> filesList = filesMapper.selectBatchIds(fileIds);
        for(Files i:filesList){
            filesService.delFile(i.getUrl(),fileUploadPath);
        }
        if (imageService.removeByIds(ids) == true
                && filesService.removeByIds(fileIds)) {
            return Result.success("删除成功");
        }
        return Result.error(Constants.CODE_400, "删除失败");
    }

    @PostMapping("/flask_image")
    @Transactional
    public Result newImage(@RequestParam MultipartFile[] files,@RequestParam String type){
        for(int i=0;i<files.length;i++) {
            String[] number = (files[i].getOriginalFilename()).split("\\.");
            String[] number2 = number[0].split("_");
            if(number.length == 0){
                return Result.error(Constants.CODE_500, "文件名出错");
            }
            //获取文件事件
            Car car = carService.getOne(new QueryWrapper<Car>()
                    .eq("number",number2[0]));
            Incident incident = incidentService.getOne(new QueryWrapper<Incident>()
                    .eq("type", type).eq("car_id",car.getCarId()));
            Confirmation confirmation = confirmationService.getOne(new QueryWrapper<Confirmation>()
                    .eq("incident_id",incident.getIncidentId()));
            if (confirmation == null) {
                return Result.error(Constants.CODE_500, "对应事件不存在");
            }

            //保存文件获取url
            String url = new String();
            try {
                url = filesService.upload(files[i], fileUploadPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Files file = filesService.getOne(new QueryWrapper<Files>().eq("url",url));
/*            if(file.getType()=="png" || file.getType()=="jpg") {*/
                Image image = new Image(confirmation.getConfirmationId(), url);
                imageService.save(image);
/*            }
            else if(file.getType()=="mp4"){
                Fragment fragment = new Fragment(confirmation.getConfirmationId(),url);
                fragmentService.save(fragment);
            }*/
        }
        return Result.success(Constants.CODE_200, "保存成功");
    }
}

