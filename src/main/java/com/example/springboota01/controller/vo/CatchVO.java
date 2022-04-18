package com.example.springboota01.controller.vo;

import com.sun.org.apache.xpath.internal.operations.Bool;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CatchVO {
    private Integer carId;
    private String number;
    private String name;
    private Boolean blacklist;
    private Integer illegalNum;
    private LocalDateTime catchTime;
}
