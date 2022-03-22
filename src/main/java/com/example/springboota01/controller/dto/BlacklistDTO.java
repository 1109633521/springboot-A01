package com.example.springboota01.controller.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BlacklistDTO {

    private Integer id;
    private Integer carId;
    private LocalDateTime createTime;

    private String number;
    private LocalDateTime time;
    private String type;
    private Integer illegalNum;
    private Boolean blacklist;
    private String color;
    private String illegalUrl;
    private String conditionUrl;


}
