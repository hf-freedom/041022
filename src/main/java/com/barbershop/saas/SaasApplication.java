package com.barbershop.saas;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.barbershop.saas.mapper")
public class SaasApplication {
    public static void main(String[] args) {
        SpringApplication.run(SaasApplication.class, args);
    }
}