package com.example.springboota01.mapper;

import com.example.springboota01.controller.dto.CatchDTO;
import com.example.springboota01.entity.Catch;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboota01.entity.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author luoxu
 * @since 2022-03-19
 */
public interface CatchMapper extends BaseMapper<Catch> {
    @Select("select DISTINCT `cross`.`name`,car.number,catch.catch_time,blacklist,illegal_num,illegal_url,condition_url " +
            "from catch natural join car join (camera natural join `cross`)using (camera_id)" +
            "ORDER BY catch.catch_time DESC limit #{pageNum},#{pageSize}")
    List<CatchDTO> selectPage(Integer pageNum, Integer pageSize);


    @Select("select count(DISTINCT catch_id) " +
            "from catch natural join car join (camera natural join `cross`)using (camera_id)")
    Integer getTotal();
}
