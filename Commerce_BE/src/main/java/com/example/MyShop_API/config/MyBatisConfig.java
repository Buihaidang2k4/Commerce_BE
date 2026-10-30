package com.example.MyShop_API.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.example.MyShop_API.mybatis.mapper")
public class MyBatisConfig {
}
