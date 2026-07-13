package com.foot.bootstrap;

import com.foot.adapter.persistence.jpa.repository.spring.PlayerSpringRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.foot")
@AutoConfigurationPackage(basePackages = "com.foot")
@EnableJpaRepositories(basePackageClasses = PlayerSpringRepository.class)
public class FootballBootstrapApplication {

    public static void main(String[] args) {
        SpringApplication.run(FootballBootstrapApplication.class, args);
    }
}

