package com.example.springboota01.service;

import com.example.springboota01.entity.Files;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author luoxu
 * @since 2022-03-22
 */
public interface IFilesService extends IService<Files> {
    public String upload(MultipartFile file,String fileUploadPath)throws IOException;
    public void download(String fileUUID, HttpServletResponse response,String fileUploadPath) throws IOException;
    public boolean delFile(String url,String fileUploadPath);
    public void showImage(String fileUUID,HttpServletResponse response,String fileUploadPath) throws IOException;
    void showVideo(String fileUUID, HttpServletResponse response, String fileUploadPath) throws IOException;

    String uploadVideo(@RequestParam MultipartFile file, String fileUploadPath) throws IOException;

    //转码
    Long videoToAudio(String filePath, String url);
}
