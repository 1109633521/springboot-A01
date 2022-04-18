package com.example.springboota01.mapper;

import com.example.springboota01.controller.dto.VideoDTO;
import com.example.springboota01.controller.dto.pageDTO.VideoPageDTO;
import com.example.springboota01.controller.vo.CarTypeData;
import com.example.springboota01.controller.vo.Counter;
import com.example.springboota01.controller.vo.CrossData;
import com.example.springboota01.entity.Video;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author luoxu
 * @since 2022-03-11
 */
@Mapper
public interface VideoMapper extends BaseMapper<Video> {
    List<VideoDTO> findPage(VideoPageDTO page);
    List<VideoDTO> findAll(String name);
    Integer findTotal(VideoPageDTO page);
    @Select("select url from incident LEFT JOIN (camera natural join `cross`)using(camera_id) LEFT JOIN video using(camera_id)\n" +
            "where `cross`.cross_id = #{crossId} ORDER BY incident.time desc limit 1")
    String getCrossVideo(Integer crossId);

    @Select("select `cross`.name,sum(person) as person,sum(car) as car ," +
            "sum(bus) as bus, sum(motorcycle) as motorcycle,sum(truck) as truck " +
            "from video join(`cross` natural join camera) using(camera_id) " +
            "WHERE `cross`.name = #{name} " +
            "and time BETWEEN #{beginTime} and #{endTime}" +
            "GROUP BY `cross`.name")
    CrossData getCrossData(String name, LocalDateTime beginTime,LocalDateTime endTime);

    Counter getFlowData(String name, LocalDateTime beginTime, LocalDateTime endTime);

    @Select("select person,car,truck,bus,motorcycle " +
            "from video join(`cross` natural join camera) using(camera_id) " +
            "where camera_id = #{cameraId} ")
    Counter getCarTypeData(Integer cameraId);
}
