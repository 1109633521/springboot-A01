package com.example.springboota01.mapper;

import com.example.springboota01.controller.dto.BlacklistDTO;
import com.example.springboota01.controller.dto.IncidentDTO;
import com.example.springboota01.entity.Incident;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author luoxu
 * @since 2022-03-19
 */
public interface IncidentMapper extends BaseMapper<Incident> {
    @Select("select distinct car.number,`cross`.`name`,camera_number," +
            "incident.type,incident.time " +
            "from incident join car using(car_id) join (camera natural join `cross`) using (camera_id) " +
            "limit #{pageNum}, #{pageSize}")
    List<IncidentDTO> findLatest(Integer pageNum, Integer pageSize);

    @Select("select count(Distinct incident_id) " +
            "from incident join car using(car_id) join (camera natural join `cross`) " +
            "using (camera_id)")
    Integer getTotal();
}
