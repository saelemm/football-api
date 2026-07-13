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
}

