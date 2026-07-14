package com.foot.adapter.persistence.jpa.repository.spring;

import com.foot.adapter.persistence.jpa.entity.TransferJpa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferSpringRepository extends JpaRepository<TransferJpa, Long> {

    Page<TransferJpa> findByPlayerId(Long playerId, Pageable pageable);

    Page<TransferJpa> findBySourceTeamIdOrTargetTeamId(Long sourceTeamId, Long targetTeamId, Pageable pageable);

    Page<TransferJpa> findBySourceTeamId(Long sourceTeamId, Pageable pageable);

    Page<TransferJpa> findByTargetTeamId(Long targetTeamId, Pageable pageable);
}

