package com.example.springboota01.controller.dto;

import com.sun.org.apache.xpath.internal.operations.Bool;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CatchDTO {
    private Integer carId;
    private String number;
    private String name;
    private Boolean blacklist;
    private Integer illegalNum;
    private String illegalUrl;
    private String conditionUrl;
    private LocalDateTime catchTime;
}
