package com.example.springboota01.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class VideoDTO {
    private Integer cameraId;
    @JsonIgnore
    private Integer crossId;
    private Integer videoId;
    private LocalTime length;
    private LocalDateTime time;
    private Integer camera_number;
    private String name;
}
