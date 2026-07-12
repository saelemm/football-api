package com.foot.adapter.persistence.jpa.repository;

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
import port.IPlayerRepository;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
    public Long save(Player player) {
        PlayerJpa saved = playerRepository.save(toJpa(player));
        return saved.getId();
    }

    @Override
    public void delete(PlayerId id) {
        playerRepository.deleteById(id.value());
    }

    @Override
    public List<Player> findByTeamId(TeamId teamId) {
        return playerRepository.findByTeam_Id(teamId.value()).stream()
            .map(PlayerRepositoryAdapter::toDomain)
            .collect(Collectors.toList());
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

