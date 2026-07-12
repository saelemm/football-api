package com.foot.adapter.persistence.jpa.repository;

import com.foot.adapter.persistence.jpa.AbstractJpaContainerTest;
import com.foot.adapter.persistence.jpa.entity.PlayerJpa;
import com.foot.adapter.persistence.jpa.entity.TeamJpa;
import com.foot.adapter.persistence.jpa.entity.TransferJpa;
import com.foot.adapter.persistence.jpa.repository.spring.PlayerSpringRepository;
import com.foot.adapter.persistence.jpa.repository.spring.TeamSpringRepository;
import com.foot.adapter.persistence.jpa.repository.spring.TransferSpringRepository;
import entity.Team;
import entity.TeamId;
import entity.TeamIdentifier;
import entity.TeamStat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests JPA de TeamRepositoryAdapter")
class TeamRepositoryAdapterTest extends AbstractJpaContainerTest {

    @Autowired
    private TeamRepositoryAdapter adapter;

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
    @DisplayName("doit sauvegarder puis retrouver une équipe par id")
    void shouldSaveAndFindById() {
        Team team = domainTeam(0L, "Real Madrid", "RM", BigDecimal.valueOf(50000.0));

        Long savedId = adapter.save(team);
        Optional<Team> loaded = adapter.findById(new TeamId(savedId));

        assertTrue(loaded.isPresent());
        assertEquals("Real Madrid", loaded.get().teamId().name());
        assertEquals("RM", loaded.get().teamId().acronym());
        assertEquals(0, BigDecimal.valueOf(50000.0).compareTo(loaded.get().teamStat().budget()));
    }

    @Test
    @DisplayName("doit retrouver une équipe par nom et acronyme")
    void shouldFindByNameAndAcronym() {
        Long savedId = adapter.save(domainTeam(0L, "Manchester City", "MC", BigDecimal.valueOf(60000.0)));

        Optional<Team> byName = adapter.findByName("Manchester City");
        Optional<Team> byAcronym = adapter.findByAcronym("MC");

        assertTrue(byName.isPresent());
        assertTrue(byAcronym.isPresent());
        assertEquals(savedId, byName.get().teamId().teamId().value());
        assertEquals(savedId, byAcronym.get().teamId().teamId().value());
    }

    @Test
    @DisplayName("doit retourner les joueurs et l'historique des transferts de l'équipe")
    void shouldLoadPlayersAndTransfersForTeam() {
        TeamJpa team = saveTeamJpa("Liverpool", "LIV", BigDecimal.valueOf(70000.0));
        TeamJpa otherTeam = saveTeamJpa("Everton", "EVE", BigDecimal.valueOf(45000.0));
        Long player1Id = savePlayerJpa(team);
        Long player2Id = savePlayerJpa(team);
        saveTransferJpa(player1Id, null, team.getId(), BigDecimal.valueOf(3000.0));
        saveTransferJpa(player2Id, team.getId(), otherTeam.getId(), BigDecimal.valueOf(3500.0));

        Team loaded = adapter.findById(new TeamId(team.getId())).orElseThrow();

        assertEquals(2, loaded.playerIds().size());
        assertEquals(2, loaded.transferHistory().size());
    }

    @Test
    @DisplayName("doit supprimer une équipe")
    void shouldDeleteTeam() {
        Long savedId = adapter.save(domainTeam(0L, "Arsenal", "ARS", BigDecimal.valueOf(42000.0)));
        assertTrue(adapter.findById(new TeamId(savedId)).isPresent());

        adapter.delete(new TeamId(savedId));

        assertTrue(adapter.findById(new TeamId(savedId)).isEmpty());
    }

    @Test
    @DisplayName("doit retourner toutes les équipes")
    void shouldFindAllTeams() {
        adapter.save(domainTeam(0L, "Team A", "TA", BigDecimal.valueOf(10000.0)));
        adapter.save(domainTeam(0L, "Team B", "TB", BigDecimal.valueOf(20000.0)));

        assertEquals(2, adapter.findAll().size());
    }

    private Team domainTeam(Long id, String name, String acronym, BigDecimal budget) {
        Date now = new Date();
        return new Team(
            new TeamIdentifier(new TeamId(id), name, acronym),
            new TeamStat(budget, now, now),
            java.util.List.of(),
            java.util.List.of()
        );
    }

    private TeamJpa saveTeamJpa(String name, String acronym, BigDecimal budget) {
        TeamJpa team = new TeamJpa();
        team.setName(name);
        team.setAcronym(acronym);
        team.setBudget(budget);
        team.setCreatedAt(new Date());
        team.setUpdatedAt(new Date());
        return teamSpringRepository.save(team);
    }

    private Long savePlayerJpa(TeamJpa team) {
        PlayerJpa player = new PlayerJpa();
        player.setFirstName("John");
        player.setLastName("Doe");
        player.setAcronym("JD");
        player.setPosition(entity.PositionEnum.CM);
        player.setPerformanceNote(7.5f);
        player.setMarketPrice(BigDecimal.valueOf(5000.0));
        player.setTitulaire(false);
        player.setTeam(team);
        player.setVersion(0);
        player.setCreatedAt(new Date());
        player.setUpdatedAt(new Date());
        return playerSpringRepository.save(player).getId();
    }

    private void saveTransferJpa(Long playerId, Long sourceTeamId, Long targetTeamId, BigDecimal price) {
        TransferJpa transfer = new TransferJpa();
        transfer.setPlayerId(playerId);
        transfer.setSourceTeamId(sourceTeamId);
        transfer.setTargetTeamId(targetTeamId);
        transfer.setTransferPrice(price);
        transfer.setTransferDate(new Date());
        transferSpringRepository.save(transfer);
    }
}

