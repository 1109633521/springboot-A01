package com.example.springboota01.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.springboota01.entity.Blacklist;
import com.example.springboota01.entity.Car;
import com.example.springboota01.mapper.BlacklistMapper;
import com.example.springboota01.service.IBlacklistService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboota01.service.ICarService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author luoxu
 * @since 2022-03-13
 */
@Service
public class BlacklistServiceImpl extends ServiceImpl<BlacklistMapper, Blacklist> implements IBlacklistService {

}
