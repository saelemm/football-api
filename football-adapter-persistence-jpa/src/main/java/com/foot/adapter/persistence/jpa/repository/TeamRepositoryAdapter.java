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
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import port.ITeamRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import pagination.PagedResult;
import org.springframework.stereotype.Repository;

@Repository
public class TeamRepositoryAdapter implements ITeamRepository {

    private final TeamSpringRepository teamRepository;
    private final PlayerSpringRepository playerRepository;
    private final TransferSpringRepository transferRepository;
    private final EntityManager entityManager;

    public TeamRepositoryAdapter(
        TeamSpringRepository teamRepository,
        PlayerSpringRepository playerRepository,
        TransferSpringRepository transferRepository,
        EntityManager entityManager
    ) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.transferRepository = transferRepository;
        this.entityManager = entityManager;
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
    @Transactional
    public Long save(Team team) {
        TeamJpa teamJpa = toJpa(team);
        TeamJpa saved;

        if (teamJpa.getId() != null) {
            TeamJpa managedEntity = entityManager.find(TeamJpa.class, teamJpa.getId());
            if (managedEntity != null) {
                int expectedCurrentVersion = Math.max(0, teamJpa.getVersion() - 1);
                if (!Integer.valueOf(expectedCurrentVersion).equals(managedEntity.getVersion())) {
                    throw new OptimisticLockException(
                        "Optimistic lock conflict for team " + teamJpa.getId()
                            + " (expected version " + expectedCurrentVersion
                            + ", actual version " + managedEntity.getVersion() + ")"
                    );
                }

                managedEntity.setName(teamJpa.getName());
                managedEntity.setAcronym(teamJpa.getAcronym());
                managedEntity.setBudget(teamJpa.getBudget());
                managedEntity.setUpdatedAt(teamJpa.getUpdatedAt());
                saved = managedEntity;
                entityManager.flush();
            } else {
                saved = teamRepository.save(teamJpa);
            }
        } else {
            saved = teamRepository.save(teamJpa);
        }

        return saved.getId();
    }

    @Override
    public void delete(TeamId id) {
        teamRepository.deleteById(id.value());
    }

    @Override
    public PagedResult<Team> findAll(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortField = mapTeamSortField(sortBy);
        Page<TeamJpa> teamPage = teamRepository.findAll(PageRequest.of(page, size, Sort.by(sortDirection, sortField)));

        List<Team> content = teamPage
            .stream()
            .map(this::toDomain)
            .collect(Collectors.toList());

        return new PagedResult<>(
            content,
            teamPage.getNumber(),
            teamPage.getSize(),
            teamPage.getTotalElements(),
            teamPage.getTotalPages(),
            teamPage.isFirst(),
            teamPage.isLast(),
            sortField,
            sortDirection.name().toLowerCase()
        );
    }

    private String mapTeamSortField(String sortBy) {
        if (sortBy == null) {
            return "name";
        }

        return switch (sortBy.toLowerCase()) {
            case "name", "nom" -> "name";
            case "acronym", "acronyme" -> "acronym";
            case "budget" -> "budget";
            default -> "name";
        };
    }

    private Team toDomain(TeamJpa jpa) {
        TeamId teamId = new TeamId(jpa.getId());

        List<PlayerId> playerIds = playerRepository.findByTeam_Id(jpa.getId(), PageRequest.of(0, Integer.MAX_VALUE)).stream()
            .map(p -> new PlayerId(p.getId()))
            .collect(Collectors.toList());

        List<Transfer> transferHistory = transferRepository
            .findBySourceTeamIdOrTargetTeamId(jpa.getId(), jpa.getId(), PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.DESC, "transferDate")))
            .stream()
            .map(TransferRepositoryAdapter::toDomain)
            .collect(Collectors.toList());

        return new Team(
            new TeamIdentifier(teamId, jpa.getName(), jpa.getAcronym()),
            new TeamStat(jpa.getBudget(), jpa.getCreatedAt(), jpa.getUpdatedAt(), jpa.getVersion()),
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
        jpa.setVersion(team.teamStat().version() == null ? 0 : team.teamStat().version());
        jpa.setCreatedAt(team.teamStat().creation());
        jpa.setUpdatedAt(team.teamStat().lastUpdate());
        return jpa;
    }
}

