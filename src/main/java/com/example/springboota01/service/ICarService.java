package com.example.springboota01.service;

import com.example.springboota01.entity.Car;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author luoxu
 * @since 2022-03-18
 */
public interface ICarService extends IService<Car> {
    public void blackCheck(Integer id);
}
