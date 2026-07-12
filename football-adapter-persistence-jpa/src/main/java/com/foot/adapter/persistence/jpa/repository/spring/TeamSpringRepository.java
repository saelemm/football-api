package com.foot.adapter.persistence.jpa.repository.spring;

import com.foot.adapter.persistence.jpa.entity.TeamJpa;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamSpringRepository extends JpaRepository<TeamJpa, Long> {

    Optional<TeamJpa> findByName(String name);

    Optional<TeamJpa> findByAcronym(String acronym);
}

