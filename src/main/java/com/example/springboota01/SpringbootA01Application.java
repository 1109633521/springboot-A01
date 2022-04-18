package com.example.springboota01;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.oas.annotations.EnableOpenApi;

import javax.swing.*;

@RestController
@SpringBootApplication
@EnableOpenApi
@EnableTransactionManagement
public class SpringbootA01Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootA01Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Spring.class);
    }

    // 加载YML格式自定义配置文件
    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
//		yaml.setResources(new FileSystemResource(ResourceUtils.CLASSPATH_URL_PREFIX + "permission.yml"));//File引入
        yaml.setResources(new ClassPathResource("fileupload.yml"));//class引入，避免了路径处理问题
        configurer.setProperties(yaml.getObject());
        return configurer;
    }

    // 继承SpringBootServletInitializer 实现configure 方便打war 外部服务器部署。

}
