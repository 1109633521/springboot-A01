package com.example.springboota01.mapper;

import com.example.springboota01.controller.dto.VideoDTO;
import com.example.springboota01.controller.dto.pageDTO.VideoPageDTO;
import com.example.springboota01.entity.Video;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author luoxu
 * @since 2022-03-11
 */
@Mapper
public interface VideoMapper extends BaseMapper<Video> {
    List<VideoDTO> findPage(VideoPageDTO page);
    List<VideoDTO> findAll(String name);
    Integer findTotal(VideoPageDTO page);
}
