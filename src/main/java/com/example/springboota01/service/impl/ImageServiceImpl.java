package com.example.springboota01.service.impl;

import com.example.springboota01.entity.Image;
import com.example.springboota01.mapper.ImageMapper;
import com.example.springboota01.service.IImageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author luoxu
 * @since 2022-03-25
 */
@Service
public class ImageServiceImpl extends ServiceImpl<ImageMapper, Image> implements IImageService {

}
