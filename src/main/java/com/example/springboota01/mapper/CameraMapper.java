package com.example.springboota01.mapper;

import com.example.springboota01.entity.Camera;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author luoxu
 * @since 2022-03-11
 */
public interface CameraMapper extends BaseMapper<Camera> {
    @Select("select max(camera_number) " +
            "from camera " +
            "where cross_id=#{crossId}")
    public Integer getNumber(Integer crossId);
}
