package com.example.springboota01.controller.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.tomcat.jni.Local;

import java.time.LocalDateTime;

@Setter
@Getter
public class ConfirmDTO {
    private Integer confirmationId;
    private Integer incidentId;
    private Integer carId;
    private Integer cameraId;
    private Integer cameraNumber;
    private String type;
    private String name;
    private LocalDateTime confirmTime;
    private String number;
    private Boolean isConfirm;
    private Boolean isDeal;
    private LocalDateTime createTime;
    private String userName;
    private String imageUrl;
    private String fragmentUrl;

    private Double centerLng;
    private Double centerLat;
}
