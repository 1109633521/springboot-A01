package com.example.springboota01.service;

import com.example.springboota01.controller.dto.UserDTO;
import com.example.springboota01.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author luoxu
 * @since 2022-03-13
 */
public interface IUserService extends IService<User> {

    UserDTO login(UserDTO userDTO);

    User register(User user);
}
