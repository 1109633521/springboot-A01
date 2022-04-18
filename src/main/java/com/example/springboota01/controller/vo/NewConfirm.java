package com.example.springboota01.controller.vo;

import lombok.Data;

import java.util.Map;

@Data
public class NewConfirm {
    private Integer camera_id;
    private Map<String, String[]> overspeed;
    private Map<String, String[]> car_break_red;
    private Map<String, String[]> online;
    private Map<String, String[]> pedestrian_red;
    private Map<String, String[]> reverse;
    private Map<String, String[]> pdestrian_blocked;
    private Map<String, String[]> wrong_way;
    private Map<String, String[]> turn_round;
    private Map<String, String[]> illegal_park;
    private Counter counter;
    private VidInformation vid_information;
}

