package com.example.springboota01.controller.vo;

import io.swagger.models.auth.In;
import lombok.Data;

@Data
public class CarAndPerson {
    private Integer car;
    private Integer person;

    public CarAndPerson(int i,int j) {
        this.car = i;
        this.person = j;
    }

    public CarAndPerson() {
    }
}
