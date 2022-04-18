package com.example.springboota01.mapper;

import com.example.springboota01.controller.vo.CarData;
import com.example.springboota01.controller.vo.CarTypeData;
import com.example.springboota01.entity.Car;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author luoxu
 * @since 2022-03-18
 */
public interface CarMapper extends BaseMapper<Car> {
    @Select("select car_id,car.number,illegal_num,sum(confirmation.is_deal) as deal " +
            "from car left join incident using(car_id) left join confirmation using(incident_id) " +
            "where car.number = #{number} " +
            "GROUP BY car_id ")
    public CarData getCarData(String number);

    @Select("select ROW_NUMBER() OVER (ORDER BY count(type) DESC) as id,type,count(type) as count " +
            "from car left join catch using(car_id) " +
            "where camera_id = #{cameraId} " +
            "GROUP BY type " +
            "ORDER BY count(type) desc")
    public List<CarTypeData> getCarTypeData(Integer cameraId);
}
