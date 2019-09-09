package com.xy.attence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AttenceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AttenceApplication.class, args);
    }
}
