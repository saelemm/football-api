package com.foot.adapter.persistence.jpa.repository.spring;

import com.foot.adapter.persistence.jpa.entity.TransferJpa;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferSpringRepository extends JpaRepository<TransferJpa, Long> {

    List<TransferJpa> findByPlayerId(Long playerId);

    List<TransferJpa> findBySourceTeamIdOrTargetTeamId(Long sourceTeamId, Long targetTeamId);

    List<TransferJpa> findBySourceTeamId(Long sourceTeamId);

    List<TransferJpa> findByTargetTeamId(Long targetTeamId);
}

