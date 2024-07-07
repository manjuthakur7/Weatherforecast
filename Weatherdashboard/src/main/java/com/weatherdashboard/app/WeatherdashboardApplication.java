package com.weatherdashboard.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.weatherdashboard")
public class WeatherdashboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherdashboardApplication.class, args);
    }
}

