package com.example.springboota01.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboota01.common.Constants;
import com.example.springboota01.common.Result;
import com.example.springboota01.entity.*;
import com.example.springboota01.mapper.ConfirmationMapper;
import com.example.springboota01.mapper.FilesMapper;
import com.example.springboota01.mapper.FragmentMapper;
import com.example.springboota01.mapper.VideoMapper;
import com.example.springboota01.service.*;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author luoxu
 * @since 2022-03-26
 */
@RestController
@RequestMapping("/fragment")
@Api(tags="fragment片段类：违章视频片段管理器")
public class FragmentController {
    @Value("${files.upload.path_fragment}")
    private String fileUploadPath;

    @Resource
    private IFragmentService fragmentService;

    @Resource
    private FragmentMapper fragmentMapper;

    @Resource
    private IVideoService videoService;

    @Resource
    private VideoMapper videoMapper;

    @Resource
    private IConfirmationService confirmationService;

    @Resource
    private ConfirmationMapper confirmationMapper;

    @Resource
    private IFilesService filesService;

    @Resource
    private ICarService carService;

    @Resource
    private IIncidentService incidentService;

    @Resource
    private FilesMapper filesMapper;

    @PostMapping("/upload")
    @ApiOperation("传递一个对应事件id")
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
        Fragment fragment = new Fragment(confirmationId,url);
        fragmentService.save(fragment);
        Fragment fragment02 = fragmentService.getById(fragment.getFragmentId());
        return Result.success(Constants.CODE_200,"保存成功",fragment02);
    }

    @GetMapping("/show/{fileUUID}")
    @ApiOperation("片段视频展示")
    public void show(@PathVariable String fileUUID, HttpServletResponse response) throws IOException {
        filesService.showVideo(fileUUID,response,fileUploadPath);
    }

    @GetMapping("/getFramentUrl")
    @ApiOperation("输入confirmation事件id获得视频Url")
    public Result showImageWithConfirm(@RequestParam Integer confirmationId) throws IOException {
        Confirmation confirmation = confirmationMapper.selectById(confirmationId);
        if(confirmation == null){ return Result.error(Constants.CODE_500,"不存在对应事件");}
        Fragment fragment = fragmentMapper.selectOne(new QueryWrapper<Fragment>().eq("confirmation_id",confirmationId));
        if(fragment == null){ return Result.error(Constants.CODE_500,"不存在对应视频");}
        return Result.success(Constants.CODE_200,"获取成功",fragment);
    }

    @ApiOperation("视频单个删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "fragment_id", required = true)
    })
    @GetMapping("/del/single")
    public Result delete(@RequestParam int id) {
        Fragment fragment = fragmentMapper.selectById(id);
        if (fragment == null) {
            return Result.error(Constants.CODE_500, "没有对应id的视频片段资源，删除失败");
        }
        Files files = filesService.getOne(new QueryWrapper<Files>().eq("url", fragment.getUrl()));
        if (files == null) {
            fragmentMapper.deleteById(fragment);
            return Result.success(Constants.CODE_200, "视频资源对应文件已不存在，fragment已删除");
        }
        if (filesService.delFile(fragment.getUrl(),fileUploadPath) == false) {
            return Result.error(Constants.CODE_400, "系统错误，没有找到资源文件");
        }
        fragmentMapper.deleteById(fragment.getFragmentId());
        filesMapper.deleteById(files);
        return Result.success(Constants.CODE_200, "删除成功");
    }

    // 根据id批量删除
    @ApiOperation("批量删除视频")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "video_id", required = true)
    })
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {

        List<Fragment> fragmentList = fragmentMapper.selectBatchIds(ids);
        List<Integer> fileIds = new ArrayList<>();
        for (Fragment i:fragmentList) {
            QueryWrapper<Files> queryFiles = new QueryWrapper<>();
            queryFiles.eq("url",i.getUrl());
            Files files = filesMapper.selectOne(queryFiles);
            fileIds.add(files.getId());
        }
        List<Files> filesList = filesMapper.selectBatchIds(fileIds);
        for(Files i:filesList){
            filesService.delFile(i.getUrl(),fileUploadPath);
        }
        if (fragmentService.removeByIds(ids) == true
                && filesService.removeByIds(fileIds)) {
            return Result.success("删除成功");
        }
        return Result.error(Constants.CODE_400, "删除失败");
    }

    @PostMapping("/flask_fragment")
    @Transactional
    public Result newImage(@RequestParam MultipartFile[] files,@RequestParam String type){
        for(int i=0;i<files.length;i++) {
            String[] number = (files[i].getOriginalFilename()).split("\\.");
            if(number.length == 0){
                return Result.error(Constants.CODE_500, "文件名出错");
            }
            //获取文件事件
            Car car = carService.getOne(new QueryWrapper<Car>()
                    .eq("number",number[0]));
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
            Fragment fragment = new Fragment(confirmation.getConfirmationId(), url);
            fragmentService.save(fragment);
        }
        return Result.success(Constants.CODE_200, "保存成功");
    }
}


