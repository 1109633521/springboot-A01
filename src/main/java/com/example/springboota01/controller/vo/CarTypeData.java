package com.example.springboota01.controller.vo;

import lombok.Data;

@Data
public class CarTypeData {
    private Integer id;
    private String type;
    private Integer count;

    public CarTypeData(){

    }
    public CarTypeData(String type, Integer count) {
        this.type = type;
        this.count = count;
    }

    public CarTypeData(Integer id,String type, Integer count) {
        this.id = id;
        this.type = type;
        this.count = count;
    }
}
