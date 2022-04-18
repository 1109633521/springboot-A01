package com.example.springboota01.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.springboota01.entity.Files;
import com.example.springboota01.mapper.FilesMapper;
import com.example.springboota01.service.IFilesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author luoxu
 * @since 2022-03-22
 */
@Service
public class FilesServiceImpl extends ServiceImpl<FilesMapper, Files> implements IFilesService {
    @Resource
    private FilesMapper filesMapper;

    @Override
    public String upload(@RequestParam MultipartFile file, String fileUploadPath) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String type = FileUtil.extName(originalFilename);
        long size = file.getSize();

        // 定义一个文件唯一的标识码
        String uuid = IdUtil.fastSimpleUUID();
        String fileUUID = uuid + StrUtil.DOT + type;

        File uploadFile = new File(fileUploadPath + fileUUID);
        // 判断配置的文件目录是否存在，若不存在则创建一个新的文件目录
        File parentFile = uploadFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }

        String url;
        // 获取文件的md5
        String md5 = SecureUtil.md5(file.getInputStream());
        // 从数据库查询是否存在相同的记录
        //Files dbFiles = getFileByMd5(md5);
        // 上传文件到磁盘
        file.transferTo(uploadFile);
        url = fileUUID;

        // 存储数据库
        Files saveFile = new Files();
        saveFile.setName(originalFilename);
        saveFile.setType(type);
        saveFile.setSize(size / 1024);
        saveFile.setUrl(url);
        saveFile.setMd5(md5);
        filesMapper.insert(saveFile);

        return url;
    }

    /**
     * 文件下载接口   http://localhost:9090/files/{fileUUID}
     *
     * @param fileUUID
     * @param response
     * @throws IOException
     */

    @Override
    public void download(@PathVariable String fileUUID, HttpServletResponse response, String fileUploadPath) throws IOException {

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

    /**
     * 通过文件的md5查询文件
     *
     * @param md5
     * @return
     */
    private Files getFileByMd5(String md5) {
        // 查询文件的md5是否存在
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("md5", md5);
        List<Files> filesList = filesMapper.selectList(queryWrapper);
        return filesList.size() == 0 ? null : filesList.get(0);
    }

    //文件删除
    @Override
    public boolean delFile(String url, String fileUploadPath) {
        File delFile = new File(fileUploadPath + url);
        if (delFile.isFile() && delFile.exists()) {
            delFile.delete();
            return true;
        } else {
            return false;
        }
    }

    //图片展示
    @Override
    public void showImage(String fileUUID, HttpServletResponse response, String fileUploadPath) throws IOException {
        File file = new File(fileUploadPath + fileUUID);
        FileInputStream is = new FileInputStream(file);
        if (is != null) {
            Files files = filesMapper.selectOne(new QueryWrapper<Files>().eq("url", fileUUID));
            int i = is.available();
            byte data[] = new byte[i];
            is.read(data);
            is.close();
            response.setContentType("image/jpeg");//设置返回文件类型
            OutputStream toClient = response.getOutputStream();  //的导向客户端输出二进制数据的对象
            toClient.write(data); //输出数据
            toClient.close();
        }
    }

    //视频播放
    @Override
    public void showVideo(String fileUUID, HttpServletResponse response, String fileUploadPath) throws IOException {
        // 根据文件的唯一标识码获取文件
        File file = new File(fileUploadPath + fileUUID);
        try {
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
    }

    @Override
    public String uploadVideo(@RequestParam MultipartFile file, String fileUploadPath) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String type = FileUtil.extName(originalFilename);
        long size = file.getSize();

        // 定义一个文件唯一的标识码
        String uuid = IdUtil.fastSimpleUUID();
        String fileUUID = uuid + StrUtil.DOT + type;

        File uploadFile = new File(fileUploadPath + fileUUID);
        // 判断配置的文件目录是否存在，若不存在则创建一个新的文件目录
        File parentFile = uploadFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }

        String url;
        // 获取文件的md5
        String md5 = SecureUtil.md5(file.getInputStream());
        // 从数据库查询是否存在相同的记录
        //Files dbFiles = getFileByMd5(md5);
        // 上传文件到磁盘
        file.transferTo(uploadFile);
        url = fileUUID;

        //转码，并转换URL
        Long length = videoToAudio(fileUploadPath,url);
        if(length == 0){
            return null;
        }
        // 存储数据库
        Files saveFile = new Files();
        saveFile.setName(originalFilename);
        saveFile.setType(type);
        saveFile.setSize(length / 1024);
        saveFile.setUrl("0" + url);
        saveFile.setMd5(md5);
        filesMapper.insert(saveFile);

        return "0" + url;
    }

    //转码
    @Override
    public Long videoToAudio(String filePath, String url) {
        File file = new File(filePath + url);
        File target = new File(filePath + "0" +url);
        try {
            // 设置输出流的格式
            AudioAttributes audio = new AudioAttributes();
            audio.setCodec("libmp3lame");//音频编码格式
            //audio.setBitRate(new Integer(56000));//设置比特率，比特率越大，转出来的音频越大（默认是128000，最好默认就行，有特殊要求再设置）
            audio.setChannels(new Integer(1));
            audio.setSamplingRate(new Integer(22050));
            VideoAttributes video = new VideoAttributes();
            video.setCodec("libx264");//视屏编码格式
            //video.setBitRate(new Integer(56000));//设置比特率，比特率越大，转出来的视频越大（默认是128000,最好默认就行，有特殊要求再设置）
            video.setFrameRate(new Integer(15));//数值设置小了，视屏会卡顿
            EncodingAttributes attrs = new EncodingAttributes();
            attrs.setFormat("mp4");
            attrs.setAudioAttributes(audio);
            attrs.setVideoAttributes(video);
            Encoder encoder = new Encoder();
            MultimediaObject multimediaObject = new MultimediaObject(file);
            try {
                encoder.encode(multimediaObject, target, attrs);
                System.out.println("转换成功！");
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InputFormatException e) {
                e.printStackTrace();
            } catch (EncoderException e) {
                e.printStackTrace();
            }
        } finally {

        }
        file.delete();
        return target.length();
    }
}
