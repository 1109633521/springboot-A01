package com.example.springboota01.service.impl;

import com.example.springboota01.entity.Video;
import com.example.springboota01.mapper.VideoMapper;
import com.example.springboota01.service.IVideoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author luoxu
 * @since 2022-04-08
 */
@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements IVideoService {

}
