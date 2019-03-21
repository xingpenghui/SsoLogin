package com.feri.ssologin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@MapperScan("com.feri.dao")
@EnableSwagger2
public class SsologinApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsologinApplication.class, args);
    }

}
