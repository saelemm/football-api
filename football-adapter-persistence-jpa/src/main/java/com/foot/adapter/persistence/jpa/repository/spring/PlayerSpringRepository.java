package com.foot.adapter.persistence.jpa.repository.spring;

import com.foot.adapter.persistence.jpa.entity.PlayerJpa;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerSpringRepository extends JpaRepository<PlayerJpa, Long> {

    List<PlayerJpa> findByTeam_Id(Long teamId);
}

