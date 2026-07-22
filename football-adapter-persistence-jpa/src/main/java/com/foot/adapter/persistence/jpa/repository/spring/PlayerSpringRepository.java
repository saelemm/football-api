package com.foot.adapter.persistence.jpa.repository.spring;

import com.foot.adapter.persistence.jpa.entity.PlayerJpa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerSpringRepository extends JpaRepository<PlayerJpa, Long> {

    Page<PlayerJpa> findByTeam_Id(Long teamId, Pageable pageable);

    Page<PlayerJpa> findByTeam_IdAndTitulaire(Long teamId, boolean titulaire, Pageable pageable);

    List<PlayerJpa> findByTeam_IdIn(List<Long> teamIds, Sort sort);
}

