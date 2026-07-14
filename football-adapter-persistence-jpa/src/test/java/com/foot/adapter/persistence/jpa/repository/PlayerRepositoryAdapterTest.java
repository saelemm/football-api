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
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;
import org.springframework.beans.factory.annotation.Autowired;
import pagination.PagedResult;

import java.math.BigDecimal;
import java.util.Date;
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

        PagedResult<Player> players = adapter.findByTeamId(new TeamId(teamId), 0, 20, "name", "asc");

        assertEquals(2, players.content().size());
    }

    @Test
    @DisplayName("doit trier les joueurs par market price descendant")
    void shouldSortPlayersByMarketPriceDescending() {
        Long teamId = saveTeam("Ajax", "AJA");
        adapter.save(domainPlayer(0L, teamId, true, "Alan", "Low", "AL", BigDecimal.valueOf(1000.0)));
        adapter.save(domainPlayer(0L, teamId, true, "Bruno", "High", "BH", BigDecimal.valueOf(5000.0)));
        adapter.save(domainPlayer(0L, teamId, true, "Chris", "Mid", "CM", BigDecimal.valueOf(3000.0)));

        PagedResult<Player> players = adapter.findByTeamId(new TeamId(teamId), 0, 10, "marketPrice", "desc");

        assertEquals(3, players.content().size());
        assertEquals("High", players.content().get(0).identifier().lastName());
        assertEquals("Mid", players.content().get(1).identifier().lastName());
        assertEquals("Low", players.content().get(2).identifier().lastName());
    }

    @Test
    @DisplayName("doit trier les joueurs par note de performance descendante")
    void shouldSortPlayersByPerformanceDescending() {
        Long teamId = saveTeam("Chelsea", "CHE");
        Date now = new Date();

        adapter.save(new Player(
            new PlayerIdentifier(new PlayerId(0L), "Alan", "Low", "AL", new TeamId(teamId)),
            new PlayerStat(PositionEnum.ST, new Note(5.5f), new Price(BigDecimal.valueOf(1000.0)), true),
            new PlayerVersion(0, now, now)
        ));
        adapter.save(new Player(
            new PlayerIdentifier(new PlayerId(0L), "Bruno", "High", "BH", new TeamId(teamId)),
            new PlayerStat(PositionEnum.ST, new Note(9.2f), new Price(BigDecimal.valueOf(5000.0)), true),
            new PlayerVersion(0, now, now)
        ));
        adapter.save(new Player(
            new PlayerIdentifier(new PlayerId(0L), "Chris", "Mid", "CM", new TeamId(teamId)),
            new PlayerStat(PositionEnum.ST, new Note(7.1f), new Price(BigDecimal.valueOf(3000.0)), true),
            new PlayerVersion(0, now, now)
        ));

        PagedResult<Player> players = adapter.findByTeamId(new TeamId(teamId), 0, 10, "performance", "desc");

        assertEquals(3, players.content().size());
        assertEquals("High", players.content().get(0).identifier().lastName());
        assertEquals("Mid", players.content().get(1).identifier().lastName());
        assertEquals("Low", players.content().get(2).identifier().lastName());
        assertEquals("performanceNote", players.sortBy());
        assertEquals("desc", players.direction());
    }

    @Test
    @DisplayName("doit paginer les joueurs par nom sur plusieurs pages")
    void shouldPaginatePlayersByNameAcrossPages() {
        Long teamId = saveTeam("Benfica", "BEN");
        adapter.save(domainPlayer(0L, teamId, true, "A", "Alpha", "AA", BigDecimal.valueOf(1000.0)));
        adapter.save(domainPlayer(0L, teamId, true, "B", "Bravo", "BB", BigDecimal.valueOf(2000.0)));
        adapter.save(domainPlayer(0L, teamId, true, "C", "Charlie", "CC", BigDecimal.valueOf(3000.0)));

        PagedResult<Player> secondPage = adapter.findByTeamId(new TeamId(teamId), 1, 2, "name", "asc");

        assertEquals(1, secondPage.content().size());
        assertEquals("Charlie", secondPage.content().getFirst().identifier().lastName());
        assertEquals(3, secondPage.totalElements());
        assertEquals(2, secondPage.totalPages());
        assertFalse(secondPage.first());
        assertTrue(secondPage.last());
    }

    @Test
    @DisplayName("doit paginer les titulaires d'une équipe côté SQL")
    void shouldPaginateStartersByTeamId() {
        Long teamId = saveTeam("Nice", "OGCN");
        adapter.save(domainPlayer(0L, teamId, true));
        adapter.save(domainPlayer(0L, teamId, false));
        adapter.save(domainPlayer(0L, teamId, true));

        PagedResult<Player> starters = adapter.findByTeamIdAndTitulaire(new TeamId(teamId), true, 0, 10, "name", "asc");

        assertEquals(2, starters.content().size());
        assertTrue(starters.content().stream().allMatch(player -> player.stats().isTitulaire()));
        assertEquals(2, starters.totalElements());
    }

    @Test
    @DisplayName("doit paginer les remplaçants d'une équipe côté SQL")
    void shouldPaginateSubstitutesByTeamId() {
        Long teamId = saveTeam("Lens", "RCL");
        adapter.save(domainPlayer(0L, teamId, true));
        adapter.save(domainPlayer(0L, teamId, false));
        adapter.save(domainPlayer(0L, teamId, false));

        PagedResult<Player> substitutes = adapter.findByTeamIdAndTitulaire(new TeamId(teamId), false, 0, 1, "marketPrice", "desc");

        assertEquals(1, substitutes.content().size());
        assertFalse(substitutes.content().getFirst().stats().isTitulaire());
        assertEquals(2, substitutes.totalElements());
        assertEquals("marketPrice", substitutes.sortBy());
    }

    @Test
    @DisplayName("doit filtrer les titulaires et les trier par acronyme")
    void shouldFilterAndSortStartersByAcronym() {
        Long teamId = saveTeam("Porto", "POR");
        adapter.save(domainPlayer(0L, teamId, true, "Alex", "Zulu", "ZZ", BigDecimal.valueOf(2000.0)));
        adapter.save(domainPlayer(0L, teamId, false, "Ben", "Bench", "AA", BigDecimal.valueOf(9000.0)));
        adapter.save(domainPlayer(0L, teamId, true, "Carl", "Starter", "BB", BigDecimal.valueOf(1000.0)));

        PagedResult<Player> starters = adapter.findByTeamIdAndTitulaire(new TeamId(teamId), true, 0, 10, "acronym", "asc");

        assertEquals(2, starters.content().size());
        assertTrue(starters.content().stream().allMatch(player -> player.stats().isTitulaire()));
        assertEquals("BB", starters.content().get(0).identifier().acronym());
        assertEquals("ZZ", starters.content().get(1).identifier().acronym());
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
        adapter.save(titulairePlayer.updateStats(titulairePlayer.stats().removeTitularisation()));

        Player updatedPlayer = adapter.findById(new PlayerId(playerId)).orElseThrow();

        assertFalse(updatedPlayer.stats().isTitulaire());
        assertEquals(teamId, updatedPlayer.identifier().teamId().value());
    }

    @Test
    @DisplayName("doit incrémenter automatiquement la version lors d'une mise à jour")
    void shouldIncrementVersionWhenUpdating() {
        Long teamId = saveTeam("Marseille", "OM");
        Long playerId = adapter.save(domainPlayer(0L, teamId, false));

        Player loadedPlayer = adapter.findById(new PlayerId(playerId)).orElseThrow();
        int initialVersion = loadedPlayer.version().version();

        // Mise a jour avec changement effectif d'etat
        Player updatedDomainPlayer = loadedPlayer.updateStats(loadedPlayer.stats().assignTitularisation());
        adapter.save(updatedDomainPlayer);

        // Recharger et vérifier que la version a été incrémentée par Hibernate
        Player reloadedPlayer = adapter.findById(new PlayerId(playerId)).orElseThrow();
        assertEquals(initialVersion + 1, reloadedPlayer.version().version());
    }

    @Test
    @DisplayName("doit mettre à jour automatiquement updatedAt lors d'une modification")
    void shouldUpdateTimestampWhenModifying() {
        Long teamId = saveTeam("Lille", "LOSC");
        Long playerId = adapter.save(domainPlayer(0L, teamId, false));

        Player loadedPlayer = adapter.findById(new PlayerId(playerId)).orElseThrow();

        // Mise a jour avec changement effectif d'etat
        Player updatedDomainPlayer = loadedPlayer.updateStats(loadedPlayer.stats().assignTitularisation());
        Date expectedUpdatedAt = updatedDomainPlayer.version().updatedAt();
        adapter.save(updatedDomainPlayer);

        // Verifier que la valeur calculee par le domaine est persistee.
        Player reloadedPlayer = adapter.findById(new PlayerId(playerId)).orElseThrow();
        assertEquals(expectedUpdatedAt.getTime(), reloadedPlayer.version().updatedAt().getTime());
    }

    @Test
    @DisplayName("doit conserver createdAt lors des mises à jour")
    void shouldPreserveCreatedAtWhenUpdating() throws InterruptedException {
        Long teamId = saveTeam("Saint-Etienne", "ASSE");
        Long playerId = adapter.save(domainPlayer(0L, teamId, false));

        Player loadedPlayer = adapter.findById(new PlayerId(playerId)).orElseThrow();
        Date initialCreatedAt = loadedPlayer.version().createdAt();

        Thread.sleep(100);

        // Mise à jour
        Player updatedDomainPlayer = loadedPlayer.updateStats(loadedPlayer.stats().removeTitularisation());
        adapter.save(updatedDomainPlayer);

        // Vérifier que createdAt n'a pas changé
        Player reloadedPlayer = adapter.findById(new PlayerId(playerId)).orElseThrow();
        assertEquals(initialCreatedAt, reloadedPlayer.version().createdAt());
    }

    @Test
    @DisplayName("doit incrémenter la version lors d'un transfert")
    void shouldIncrementVersionWhenTransferring() {
        Long sourceTeamId = saveTeam("Bordeaux", "FCGB");
        Long targetTeamId = saveTeam("Nantes", "FCN");
        Long playerId = adapter.save(domainPlayer(0L, sourceTeamId, false));

        Player loadedPlayer = adapter.findById(new PlayerId(playerId)).orElseThrow();
        int initialVersion = loadedPlayer.version().version();

        // Transfert
        Player transferredPlayer = loadedPlayer.transferTo(new TeamId(targetTeamId));
        adapter.save(transferredPlayer);

        // Vérifier que la version a été incrémentée et que l'équipe a changé
        Player reloadedPlayer = adapter.findById(new PlayerId(playerId)).orElseThrow();
        assertEquals(initialVersion + 1, reloadedPlayer.version().version());
        assertEquals(targetTeamId, reloadedPlayer.identifier().teamId().value());
    }

    @Test
    @DisplayName("doit lever une OptimisticLockException avec une version obsolete")
    void shouldThrowOptimisticLockExceptionOnStaleVersion() {
        Long teamId = saveTeam("Rennes", "SRFC");
        Long playerId = adapter.save(domainPlayer(0L, teamId, false));

        Player stalePlayer = adapter.findById(new PlayerId(playerId)).orElseThrow();

        // First update succeeds (version 0 -> 1).
        Player firstUpdate = stalePlayer.updateStats(stalePlayer.stats().assignTitularisation());
        adapter.save(firstUpdate);

        // Reusing stale aggregate tries version 0 -> 1 again and must fail.
        Player staleUpdate = stalePlayer.updateStats(stalePlayer.stats().removeTitularisation());

        assertThrows(JpaOptimisticLockingFailureException.class, () -> adapter.save(staleUpdate));
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

    private Player domainPlayer(Long id, Long teamId, boolean titulaire, String firstName, String lastName, String acronym, BigDecimal marketPrice) {
        Date now = new Date();
        return new Player(
            new PlayerIdentifier(new PlayerId(id), firstName, lastName, acronym, new TeamId(teamId)),
            new PlayerStat(PositionEnum.ST, new Note(8.5f), new Price(marketPrice), titulaire),
            new PlayerVersion(0, now, now)
        );
    }
}

