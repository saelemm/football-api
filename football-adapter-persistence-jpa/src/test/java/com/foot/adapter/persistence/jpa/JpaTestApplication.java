package com.foot.adapter.persistence.jpa;

import com.foot.adapter.persistence.jpa.repository.PlayerRepositoryAdapter;
import com.foot.adapter.persistence.jpa.repository.TeamRepositoryAdapter;
import com.foot.adapter.persistence.jpa.repository.TransferRepositoryAdapter;
import com.foot.adapter.persistence.jpa.repository.spring.PlayerSpringRepository;
import com.foot.adapter.persistence.jpa.repository.spring.TeamSpringRepository;
import com.foot.adapter.persistence.jpa.repository.spring.TransferSpringRepository;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootConfiguration
@EnableAutoConfiguration
@EnableJpaRepositories(basePackageClasses = {
    TeamSpringRepository.class,
    PlayerSpringRepository.class,
    TransferSpringRepository.class
})
@ComponentScan(basePackageClasses = {
    TeamRepositoryAdapter.class,
    PlayerRepositoryAdapter.class,
    TransferRepositoryAdapter.class
})
public class JpaTestApplication {
}

