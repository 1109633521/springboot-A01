package com.example.springboota01.service;

import com.example.springboota01.entity.Files;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
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
    public String upload(@RequestParam MultipartFile file)throws IOException;
    public void download(@PathVariable String fileUUID, HttpServletResponse response) throws IOException;
    public Boolean delete(@RequestParam Integer id);
}
