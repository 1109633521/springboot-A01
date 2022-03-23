package com.example.springboota01.mapper;

import com.example.springboota01.controller.dto.ConfirmDTO;
import com.example.springboota01.controller.dto.pageDTO.ConfirmPageDTO;
import com.example.springboota01.entity.Confirmation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author luoxu
 * @since 2022-03-19
 */
public interface ConfirmationMapper extends BaseMapper<Confirmation> {
    List<ConfirmDTO> findPage(ConfirmPageDTO page);
    Integer findTotal(ConfirmPageDTO page);
}
