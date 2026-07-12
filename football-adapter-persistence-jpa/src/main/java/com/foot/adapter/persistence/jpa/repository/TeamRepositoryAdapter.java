package com.foot.adapter.persistence.jpa.repository;

import com.foot.adapter.persistence.jpa.entity.TeamJpa;
import com.foot.adapter.persistence.jpa.repository.spring.PlayerSpringRepository;
import com.foot.adapter.persistence.jpa.repository.spring.TeamSpringRepository;
import com.foot.adapter.persistence.jpa.repository.spring.TransferSpringRepository;
import entity.PlayerId;
import entity.Team;
import entity.TeamId;
import entity.TeamIdentifier;
import entity.TeamStat;
import entity.Transfer;
import port.ITeamRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class TeamRepositoryAdapter implements ITeamRepository {

    private final TeamSpringRepository teamRepository;
    private final PlayerSpringRepository playerRepository;
    private final TransferSpringRepository transferRepository;

    public TeamRepositoryAdapter(
        TeamSpringRepository teamRepository,
        PlayerSpringRepository playerRepository,
        TransferSpringRepository transferRepository
    ) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.transferRepository = transferRepository;
    }

    @Override
    public Optional<Team> findById(TeamId id) {
        return teamRepository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public Optional<Team> findByName(String name) {
        return teamRepository.findByName(name).map(this::toDomain);
    }

    @Override
    public Optional<Team> findByAcronym(String acronym) {
        return teamRepository.findByAcronym(acronym).map(this::toDomain);
    }

    @Override
    public Long save(Team team) {
        TeamJpa saved = teamRepository.save(toJpa(team));
        return saved.getId();
    }

    @Override
    public void delete(TeamId id) {
        teamRepository.deleteById(id.value());
    }

    @Override
    public List<Team> findAll() {
        return teamRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    private Team toDomain(TeamJpa jpa) {
        TeamId teamId = new TeamId(jpa.getId());

        List<PlayerId> playerIds = playerRepository.findByTeam_Id(jpa.getId()).stream()
            .map(p -> new PlayerId(p.getId()))
            .collect(Collectors.toList());

        List<Transfer> transferHistory = transferRepository
            .findBySourceTeamIdOrTargetTeamId(jpa.getId(), jpa.getId())
            .stream()
            .map(TransferRepositoryAdapter::toDomain)
            .collect(Collectors.toList());

        return new Team(
            new TeamIdentifier(teamId, jpa.getName(), jpa.getAcronym()),
            new TeamStat(jpa.getBudget(), jpa.getCreatedAt(), jpa.getUpdatedAt()),
            playerIds,
            transferHistory
        );
    }

    private TeamJpa toJpa(Team team) {
        TeamJpa jpa = new TeamJpa();
        Long id = team.teamId().teamId().value();
        jpa.setId(id == 0L ? null : id);
        jpa.setName(team.teamId().name());
        jpa.setAcronym(team.teamId().acronym());
        jpa.setBudget(team.teamStat().budget());
        jpa.setCreatedAt(team.teamStat().creation());
        jpa.setUpdatedAt(team.teamStat().lastUpdate());
        return jpa;
    }
}

