package com.coelacanthe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing
public class CoelacantheApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoelacantheApiApplication.class, args);
    }

}
