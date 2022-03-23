package com.example.springboota01.service.impl;

import com.example.springboota01.controller.dto.VideoDTO;
import com.example.springboota01.entity.Video;
import com.example.springboota01.mapper.VideoMapper;
import com.example.springboota01.service.IVideoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author luoxu
 * @since 2022-03-11
 */
@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements IVideoService {

    public VideoMapper videoMapper;

}
