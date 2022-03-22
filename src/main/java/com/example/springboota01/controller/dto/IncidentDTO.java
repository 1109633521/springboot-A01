package com.example.springboota01.controller.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IncidentDTO {
    private String number;
    private String cameraNumber;
    private String name;
    private String type;
    private LocalDateTime time;
}
