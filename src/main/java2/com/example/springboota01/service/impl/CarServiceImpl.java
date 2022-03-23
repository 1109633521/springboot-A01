package com.example.springboota01.service.impl;

import com.example.springboota01.entity.Car;
import com.example.springboota01.mapper.CarMapper;
import com.example.springboota01.service.ICarService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author luoxu
 * @since 2022-03-18
 */
@Service
public class CarServiceImpl extends ServiceImpl<CarMapper, Car> implements ICarService {

}
