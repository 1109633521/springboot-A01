package com.example.springboota01.utils;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

/**
 * 代码生成器
 */
public class GodeGenerator {
    public static void main(String[] args){
        generate();
    }

    private static void generate() {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/test-a01?serverTimezone=GMT%2b8",
                "root", "123456")
                .globalConfig(builder -> {
                    builder.author("luoxu") // 设置作者
                            //.enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .outputDir("C:\\Users\\86158\\IdeaProjects\\springboot-A01\\src\\main\\java\\"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.example.springboota01") // 设置父包名
                            .moduleName(null) // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, "C:\\Users\\86158\\IdeaProjects\\springboot-A01\\src\\main\\resources\\mapper\\")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.entityBuilder().enableLombok();
                    //builder.mapperBuilder().enableMapperAnnotation().build();//Mapper 注解添加，这里我有包扫描所以可有可无
                    builder.controllerBuilder().enableHyphenStyle() // 开启驼峰转连字符
                            .enableRestStyle(); //开启生成@RestController控制器

                    builder.addInclude("test_admin") // 设置需要生成的表名
                            .addTablePrefix("t_", "test_"); // 设置过滤表前缀
                })
                //.templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}
