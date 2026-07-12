package com.foot.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.foot")
public class FootballBootstrapApplication {

    public static void main(String[] args) {
        SpringApplication.run(FootballBootstrapApplication.class, args);
    }
}

