package com.foot.adapter.persistence.jpa.repository;

import Validator.EnumValidator;
import com.foot.adapter.persistence.jpa.entity.PlayerJpa;
import com.foot.adapter.persistence.jpa.entity.TeamJpa;
import com.foot.adapter.persistence.jpa.repository.spring.PlayerSpringRepository;
import entity.Note;
import entity.Player;
import entity.PlayerId;
import entity.PlayerIdentifier;
import entity.PlayerStat;
import entity.PlayerVersion;
import entity.Price;
import entity.TeamId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import port.IPlayerRepository;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import pagination.PagedResult;
import org.springframework.stereotype.Repository;

@Repository
public class PlayerRepositoryAdapter implements IPlayerRepository {

    private final PlayerSpringRepository playerRepository;
    private final EntityManager entityManager;

    public PlayerRepositoryAdapter(PlayerSpringRepository playerRepository, EntityManager entityManager) {
        this.playerRepository = playerRepository;
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Player> findById(PlayerId id) {
        return playerRepository.findById(id.value()).map(PlayerRepositoryAdapter::toDomain);
    }

    @Override
    @Transactional
    public Long save(Player player) {
        PlayerJpa playerJpa = toJpa(player);
        PlayerJpa saved;

        // For updates, domain sends version n while DB currently stores n-1.
        // We verify this invariant to enforce optimistic locking at adapter level.
        if (playerJpa.getId() != null) {
            PlayerJpa managedEntity = entityManager.find(PlayerJpa.class, playerJpa.getId());
            if (managedEntity != null) {
                int expectedCurrentVersion = Math.max(0, playerJpa.getVersion() - 1);
                if (!Integer.valueOf(expectedCurrentVersion).equals(managedEntity.getVersion())) {
                    throw new OptimisticLockException(
                        "Optimistic lock conflict for player " + playerJpa.getId()
                            + " (expected version " + expectedCurrentVersion
                            + ", actual version " + managedEntity.getVersion() + ")"
                    );
                }

                managedEntity.setFirstName(playerJpa.getFirstName());
                managedEntity.setLastName(playerJpa.getLastName());
                managedEntity.setAcronym(playerJpa.getAcronym());
                managedEntity.setPosition(playerJpa.getPosition());
                managedEntity.setPerformanceNote(playerJpa.getPerformanceNote());
                managedEntity.setMarketPrice(playerJpa.getMarketPrice());
                managedEntity.setTitulaire(playerJpa.isTitulaire());
                managedEntity.setTeam(playerJpa.getTeam());
                managedEntity.setUpdatedAt(playerJpa.getUpdatedAt());
                saved = managedEntity;
                entityManager.flush();
            } else {
                saved = playerRepository.save(playerJpa);
            }
        } else {
            saved = playerRepository.save(playerJpa);
        }

        return saved.getId();
    }

    @Override
    public void delete(PlayerId id) {
        playerRepository.deleteById(id.value());
    }

    @Override
    public PagedResult<Player> findByTeamId(TeamId teamId, int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = mapSortDirection(direction);
        String sortField = mapPlayerSortField(sortBy);
        Page<PlayerJpa> playerPage = playerRepository.findByTeam_Id(
            teamId.value(),
            PageRequest.of(page, size, Sort.by(sortDirection, sortField))
        );

        return toPagedResult(playerPage, sortField, sortDirection);
    }

    @Override
    public PagedResult<Player> findByTeamIdAndTitulaire(TeamId teamId, boolean titulaire, int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = mapSortDirection(direction);
        String sortField = mapPlayerSortField(sortBy);
        Page<PlayerJpa> playerPage = playerRepository.findByTeam_IdAndTitulaire(
            teamId.value(),
            titulaire,
            PageRequest.of(page, size, Sort.by(sortDirection, sortField))
        );

        return toPagedResult(playerPage, sortField, sortDirection);
    }

    @Override
    public Map<Long, List<Player>> findByTeamIds(List<TeamId> teamIds) {
        if (teamIds == null || teamIds.isEmpty()) {
            return Map.of();
        }

        List<Long> ids = teamIds.stream().map(TeamId::value).distinct().toList();
        List<PlayerJpa> playerJpas = playerRepository.findByTeam_IdIn(
            ids,
            Sort.by(Sort.Direction.ASC, "lastName", "firstName")
        );

        Map<Long, List<Player>> grouped = new LinkedHashMap<>();
        for (PlayerJpa playerJpa : playerJpas) {
            if (playerJpa.getTeam() == null) {
                continue;
            }
            Long teamId = playerJpa.getTeam().getId();
            grouped.computeIfAbsent(teamId, ignored -> new java.util.ArrayList<>())
                .add(toDomain(playerJpa));
        }
        return grouped;
    }

    private PagedResult<Player> toPagedResult(Page<PlayerJpa> playerPage, String sortField, Sort.Direction sortDirection) {
        List<Player> content = playerPage
            .stream()
            .map(PlayerRepositoryAdapter::toDomain)
            .collect(Collectors.toList());

        return new PagedResult<>(
            content,
            playerPage.getNumber(),
            playerPage.getSize(),
            playerPage.getTotalElements(),
            playerPage.getTotalPages(),
            playerPage.isFirst(),
            playerPage.isLast(),
            sortField,
            sortDirection.name().toLowerCase()
        );
    }

    private String mapPlayerSortField(String sortBy) {
        try {
            PlayerSortByOption option = EnumValidator.fromString(PlayerSortByOption.class, normalizeSortBy(sortBy));
            return option.jpaField;
        } catch (IllegalArgumentException ignored) {
            return PlayerSortByOption.NAME.jpaField;
        }
    }

    private Sort.Direction mapSortDirection(String direction) {
        try {
            SortDirectionOption dir = EnumValidator.fromString(SortDirectionOption.class, direction == null ? "ASC" : direction);
            return dir == SortDirectionOption.DESC ? Sort.Direction.DESC : Sort.Direction.ASC;
        } catch (IllegalArgumentException ignored) {
            return Sort.Direction.ASC;
        }
    }

    private String normalizeSortBy(String sortBy) {
        if (sortBy == null) {
            return "NAME";
        }

        return switch (sortBy.toLowerCase()) {
            case "name", "nom", "lastname", "last_name" -> "NAME";
            case "acronym", "acronyme" -> "ACRONYM";
            case "marketprice", "market_price", "price", "prix" -> "MARKET_PRICE";
            case "performance", "note", "performance_note" -> "PERFORMANCE";
            default -> sortBy;
        };
    }

    private enum PlayerSortByOption {
        NAME("lastName"),
        ACRONYM("acronym"),
        MARKET_PRICE("marketPrice"),
        PERFORMANCE("performanceNote");

        private final String jpaField;

        PlayerSortByOption(String jpaField) {
            this.jpaField = jpaField;
        }
    }

    private enum SortDirectionOption {
        ASC,
        DESC
    }

    static Player toDomain(PlayerJpa jpa) {
        TeamId teamId = jpa.getTeam() == null ? null : new TeamId(jpa.getTeam().getId());
        return new Player(
            new PlayerIdentifier(
                new PlayerId(jpa.getId()),
                jpa.getFirstName(),
                jpa.getLastName(),
                jpa.getAcronym(),
                teamId
            ),
            new PlayerStat(
                jpa.getPosition(),
                new Note(jpa.getPerformanceNote()),
                new Price(jpa.getMarketPrice()),
                jpa.isTitulaire()
            ),
            new PlayerVersion(
                jpa.getVersion(),
                jpa.getCreatedAt(),
                jpa.getUpdatedAt()
            )
        );
    }

    private PlayerJpa toJpa(Player player) {
        PlayerJpa jpa = new PlayerJpa();
        Long id = player.identifier().id().value();
        jpa.setId(id == 0L ? null : id);
        jpa.setFirstName(player.identifier().firstName());
        jpa.setLastName(player.identifier().lastName());
        jpa.setAcronym(player.identifier().acronym());
        jpa.setPosition(player.stats().position());
        jpa.setPerformanceNote(player.stats().performanceNote().value());
        jpa.setMarketPrice(player.stats().marketPrice().value());
        jpa.setTitulaire(player.stats().isTitulaire());

        if (player.identifier().teamId() != null) {
            TeamJpa teamRef = entityManager.getReference(TeamJpa.class, player.identifier().teamId().value());
            jpa.setTeam(teamRef);
        } else {
            jpa.setTeam(null);
        }

        Date createdAt = player.version().createdAt() == null ? new Date() : player.version().createdAt();
        Date updatedAt = player.version().updatedAt() == null ? new Date() : player.version().updatedAt();
        Integer version = player.version().version() == null ? 0 : player.version().version();

        jpa.setVersion(version);
        jpa.setCreatedAt(createdAt);
        jpa.setUpdatedAt(updatedAt);
        return jpa;
    }
}

