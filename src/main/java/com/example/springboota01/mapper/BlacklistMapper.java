package com.example.springboota01.mapper;

import com.example.springboota01.controller.dto.BlacklistDTO;
import com.example.springboota01.entity.Blacklist;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author luoxu
 * @since 2022-03-13
 */
public interface BlacklistMapper extends BaseMapper<Blacklist> {
    @Select("select * " +
            "from car natural join blacklist " +
            "where number like #{number} limit #{pageNum}, #{pageSize}")
    List<BlacklistDTO> selectPage(Integer pageNum, Integer pageSize, String number);

    @Select("select * " +
            "from car natural join blacklist " +
            "where number like #{number} and " +
            "(time BETWEEN #{beginTime} AND #{endTime}) limit #{pageNum}, #{pageSize}")
    List<BlacklistDTO> selectTimePage(Integer pageNum, Integer pageSize, String number, LocalDateTime beginTime, LocalDateTime endTime);

    @Select("select count(*) from car natural join blacklist " +
            "where number like #{number}")
    Integer selectTotal(String number);

    @Select("select count(*) from car natural join blacklist " +
            "where number like #{number} and (time BETWEEN #{beginTime} AND #{endTime})")
    Integer selectTimeTotal(String number, LocalDateTime beginTime, LocalDateTime endTime);
}
