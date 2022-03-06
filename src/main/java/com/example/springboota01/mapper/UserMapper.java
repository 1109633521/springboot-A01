package com.example.springboota01.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboota01.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

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

/*    @Select("select * from sys_user where nickname like #{nickname} limit #{pageNum}, #{pageSize}")
    List<User> selectPage(Integer pageNum, Integer pageSize, String nickname);*/
}
