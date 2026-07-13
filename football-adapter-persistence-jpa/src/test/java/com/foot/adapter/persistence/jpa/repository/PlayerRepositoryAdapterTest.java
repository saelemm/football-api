package com.foot.adapter.persistence.jpa.repository;

import com.foot.adapter.persistence.jpa.AbstractJpaContainerTest;
import com.foot.adapter.persistence.jpa.entity.TeamJpa;
import com.foot.adapter.persistence.jpa.repository.spring.PlayerSpringRepository;
import com.foot.adapter.persistence.jpa.repository.spring.TeamSpringRepository;
import com.foot.adapter.persistence.jpa.repository.spring.TransferSpringRepository;
import entity.Note;
import entity.Player;
import entity.PlayerId;
import entity.PlayerIdentifier;
import entity.PlayerStat;
import entity.PlayerVersion;
import entity.PositionEnum;
import entity.Price;
import entity.TeamId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests JPA de PlayerRepositoryAdapter")
class PlayerRepositoryAdapterTest extends AbstractJpaContainerTest {

    @Autowired
    private PlayerRepositoryAdapter adapter;

    @Autowired
    private TeamSpringRepository teamSpringRepository;

    @Autowired
    private PlayerSpringRepository playerSpringRepository;

    @Autowired
    private TransferSpringRepository transferSpringRepository;

    @BeforeEach
    void cleanDb() {
        transferSpringRepository.deleteAll();
        playerSpringRepository.deleteAll();
        teamSpringRepository.deleteAll();
    }

    @Test
    @DisplayName("doit sauvegarder puis retrouver un joueur par id")
    void shouldSaveAndFindById() {
        Long teamId = saveTeam("PSG", "PSG");
        Player player = domainPlayer(0L, teamId, true);

        Long playerId = adapter.save(player);
        Optional<Player> loaded = adapter.findById(new PlayerId(playerId));

        assertTrue(loaded.isPresent());
        assertEquals("Kylian", loaded.get().identifier().firstName());
        assertEquals(PositionEnum.ST, loaded.get().stats().position());
        assertTrue(loaded.get().stats().isTitulaire());
    }

    @Test
    @DisplayName("doit retrouver les joueurs d'une équipe")
    void shouldFindPlayersByTeamId() {
        Long teamId = saveTeam("OM", "OM");
        adapter.save(domainPlayer(0L, teamId, true));
        adapter.save(domainPlayer(0L, teamId, false));

        List<Player> players = adapter.findByTeamId(new TeamId(teamId));

        assertEquals(2, players.size());
    }

    @Test
    @DisplayName("doit supprimer un joueur")
    void shouldDeletePlayer() {
        Long teamId = saveTeam("Lyon", "OL");
        Long playerId = adapter.save(domainPlayer(0L, teamId, false));
        assertTrue(adapter.findById(new PlayerId(playerId)).isPresent());

        adapter.delete(new PlayerId(playerId));

        assertTrue(adapter.findById(new PlayerId(playerId)).isEmpty());
    }

    @Test
    @DisplayName("doit mettre à jour le statut de titularisation d'un joueur")
    void shouldUpdatePlayerTitularisationStatus() {
        Long teamId = saveTeam("Monaco", "ASM");
        Long playerId = adapter.save(domainPlayer(0L, teamId, true));

        Player titulairePlayer = adapter.findById(new PlayerId(playerId)).orElseThrow();
        adapter.save(titulairePlayer.removeTitularisation());

        Player updatedPlayer = adapter.findById(new PlayerId(playerId)).orElseThrow();

        assertFalse(updatedPlayer.stats().isTitulaire());
        assertEquals(teamId, updatedPlayer.identifier().teamId().value());
    }

    private Long saveTeam(String name, String acronym) {
        TeamJpa team = new TeamJpa();
        team.setName(name);
        team.setAcronym(acronym);
        team.setBudget(BigDecimal.valueOf(40000.0));
        team.setCreatedAt(new Date());
        team.setUpdatedAt(new Date());
        return teamSpringRepository.save(team).getId();
    }

    private Player domainPlayer(Long id, Long teamId, boolean titulaire) {
        Date now = new Date();
        return new Player(
            new PlayerIdentifier(new PlayerId(id), "Kylian", "Mbappe", "KM", new TeamId(teamId)),
            new PlayerStat(PositionEnum.ST, new Note(8.5f), new Price(BigDecimal.valueOf(9000.0)), titulaire),
            new PlayerVersion(0, now, now)
        );
    }
}

