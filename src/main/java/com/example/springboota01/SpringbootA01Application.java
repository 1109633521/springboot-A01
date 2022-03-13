package com.example.springboota01;

import com.example.springboota01.entity.User;
import com.example.springboota01.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import springfox.documentation.oas.annotations.EnableOpenApi;

import javax.swing.*;
import java.util.List;

@RestController
@SpringBootApplication
@EnableOpenApi
public class SpringbootA01Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootA01Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Spring.class);
    }

    // 继承SpringBootServletInitializer 实现configure 方便打war 外部服务器部署。

}
