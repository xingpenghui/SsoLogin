package com.feri.ssologin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.feri.dao")
public class SsologinApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsologinApplication.class, args);
    }

}
