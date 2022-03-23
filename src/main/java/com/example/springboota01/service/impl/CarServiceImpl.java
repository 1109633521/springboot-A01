package com.example.springboota01.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.springboota01.entity.Blacklist;
import com.example.springboota01.entity.Car;
import com.example.springboota01.mapper.CarMapper;
import com.example.springboota01.service.IBlacklistService;
import com.example.springboota01.service.ICarService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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

    @Resource
    private IBlacklistService blacklistService;

    @Override
    public void blackCheck(Integer id) {
        Car car = getById(id);
        if(car.getIllegalNum()==0){
            car.setBlacklist(false);
            saveOrUpdate(car);
            QueryWrapper<Blacklist> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("car_id",id);
            Blacklist blacklist = blacklistService.getOne(queryWrapper);
            if(blacklist !=null) {
                blacklistService.removeById(blacklist.getId());
            }
        }
    }
}
