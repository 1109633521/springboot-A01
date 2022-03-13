package com.example.springboota01.controller.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 接收前端登录请求的参数
 */
@Data
@ApiModel("接收用户信息的实体类")
public class UserDTO {

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty(value = "用户昵称",required = false)
    private String nickname;

    private String type;

    private String token;

    public UserDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
