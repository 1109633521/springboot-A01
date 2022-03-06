package com.example.springboota01.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@TableName("sys_user") //联系数据库的表名
@Data //代替get和set函数
public class User {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    //@JsonIgnore //数据不交给前端
    private String password;
    private String nickname;
    private String phone;
    private String sex;
    private String email;
    @TableField("address") //指定字段名称
    private String address;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
