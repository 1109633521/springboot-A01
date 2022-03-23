package com.example.springboota01.service.impl;

import com.example.springboota01.entity.Camera;
import com.example.springboota01.mapper.CameraMapper;
import com.example.springboota01.service.ICameraService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author luoxu
 * @since 2022-03-11
 */
@Service
public class CameraServiceImpl extends ServiceImpl<CameraMapper, Camera> implements ICameraService {

}
