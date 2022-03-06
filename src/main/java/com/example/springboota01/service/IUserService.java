package com.example.springboota01.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.springboota01.controller.dto.UserDTO;
import com.example.springboota01.entity.User;

public interface IUserService extends IService<User> {

    UserDTO login(UserDTO userDTO);

    User register(UserDTO userDTO);
}