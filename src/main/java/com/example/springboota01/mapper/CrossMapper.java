package com.example.springboota01.mapper;

import com.example.springboota01.entity.Cross;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.junit.Test;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author luoxu
 * @since 2022-03-11
 */
public interface CrossMapper extends BaseMapper<Cross> {
    @Select("select * from `cross` where name = #{name}")
    public Cross getByName(String name);
    public List<Cross> getGroup(String name);
}
