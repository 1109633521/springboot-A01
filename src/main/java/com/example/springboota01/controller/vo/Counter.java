package com.example.springboota01.controller.vo;

import lombok.Data;

@Data
public class Counter {
    private Integer person;
    private Integer car;
    private Integer motorcycle;
    private Integer bus;
    private Integer truck;

    public Counter(int i, int i1, int i2, int i3, int i4) {
        this.person = i;
        this.bus = i3;
        this.car = i1;
        this.motorcycle = i2;
        this.truck = i4;
    }

    public Counter() {
    }
}
