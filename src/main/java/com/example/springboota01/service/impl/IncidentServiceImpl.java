package com.example.springboota01.service.impl;

import com.example.springboota01.entity.Incident;
import com.example.springboota01.mapper.IncidentMapper;
import com.example.springboota01.service.IIncidentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author luoxu
 * @since 2022-03-19
 */
@Service
public class IncidentServiceImpl extends ServiceImpl<IncidentMapper, Incident> implements IIncidentService {

}
