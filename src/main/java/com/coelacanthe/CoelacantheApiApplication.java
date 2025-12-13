package com.coelacanthe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CoelacantheApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoelacantheApiApplication.class, args);
    }

}
