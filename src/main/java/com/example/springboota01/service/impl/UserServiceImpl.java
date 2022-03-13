package com.example.springboota01.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.springboota01.controller.dto.UserDTO;
import com.example.springboota01.entity.User;
import com.example.springboota01.mapper.UserMapper;
import com.example.springboota01.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboota01.utils.TokenUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author luoxu
 * @since 2022-03-13
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    public boolean saveUser(User user){
        return saveOrUpdate(user);
    }

    @Override
    public UserDTO login(UserDTO userDTO) {
        User one = getUserInfo(userDTO);
        if (one != null) {
            BeanUtil.copyProperties(one,userDTO,true);
            //设置token
            String token = TokenUtils.genToken(one.getId().toString(), one.getPassword());
            userDTO.setToken(token);
            return userDTO;
        }else {
            return null;
        }
    }

    @Override
    public User register(User user) {
        UserDTO dto = new UserDTO(user.getUsername(),user.getPassword());
        User one = getUserInfo(dto);
        if (one == null) {
/*          one = new User();
            BeanUtil.copyProperties(userDTO, one, true);*/
            save(user);  // 把 copy完之后的用户对象存储到数据库
            return user;
        } else {
            return null;
        }
    }

    private User getUserInfo(UserDTO userDTO) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userDTO.getUsername());
        queryWrapper.eq("password", userDTO.getPassword());
        User one;
        try {
            one = getOne(queryWrapper); // 从数据库查询用户信息
        } catch (Exception e) {
            return null;
        }
        return one;
    }
}