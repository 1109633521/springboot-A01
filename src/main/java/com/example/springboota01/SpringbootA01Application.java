package com.example.springboota01;

import com.example.springboota01.entity.User;
import com.example.springboota01.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@SpringBootApplication
public class SpringbootA01Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootA01Application.class, args);
    }

}
