package com.example.springboota01.mapper;

import com.example.springboota01.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author luoxu
 * @since 2022-03-13
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("select * from sys_user")
    List<User> findAll();

    @Insert("INSERT into sys_user(username,password,nickname,phone,sex,email,address)" +
            " VALUES(#{username},#{password},#{nickname},#{phone},#{sex},#{email},#{address})")
    Integer insertUser(User user);

    @Delete("delete from sys_user where id = #{id}")
    Integer deleteById(@Param("id") Integer id);

    @Select("select count(*) from sys_user where nickname like #{nickname}")
    Integer selectTotal(String nickname);

    @Select("select * from sys_user where nickname like #{nickname} limit #{pageNum}, #{pageSize}")
    List<User> selectPage(Integer pageNum, Integer pageSize, String nickname);

}
