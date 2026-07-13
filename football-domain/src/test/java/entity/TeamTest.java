package entity;

import Errors.InsufficientBudgetException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de l'agrégat Team")
class TeamTest {

    private Team team;
    private TeamId teamId;
    private TeamIdentifier identifier;
    private TeamStat stat;

    @BeforeEach
    void setUp() {
        teamId = new TeamId(1L);
        identifier = new TeamIdentifier(teamId, "Manchester United", "MU");
        stat = new TeamStat(BigDecimal.valueOf(10000.0), new Date(), new Date());
        team = new Team(identifier, stat, new ArrayList<>(), new ArrayList<>());
    }

    @Test
    @DisplayName("Doit créer une Team valide")
    void shouldCreateTeam() {
        assertNotNull(team);
        assertEquals("Manchester United", team.teamId().name());
        assertEquals("MU", team.teamId().acronym());
        assertEquals(BigDecimal.valueOf(10000.0), team.teamStat().budget());
    }

    @Test
    @DisplayName("Doit lever une exception lorsque le nom de l'équipe est nul")
    void shouldThrowExceptionWhenNameIsNull() {
        TeamIdentifier invalidIdentifier = new TeamIdentifier(teamId, null, "MU");
        assertThrows(IllegalArgumentException.class, () ->
            new Team(invalidIdentifier, stat, new ArrayList<>(), new ArrayList<>())
        );
    }

    @Test
    @DisplayName("Doit lever une exception lorsque le budget est négatif")
    void shouldThrowExceptionWhenBudgetNegative() {
        TeamStat invalidStat = new TeamStat(BigDecimal.valueOf(-100.0), new Date(), new Date());
        assertThrows(IllegalArgumentException.class, () ->
            new Team(identifier, invalidStat, new ArrayList<>(), new ArrayList<>())
        );
    }

    @Test
    @DisplayName("Doit autoriser une Team avec un budget nul")
    void shouldAllowTeamWithZeroBudget() {
        TeamStat zeroStat = new TeamStat(BigDecimal.ZERO, new Date(), new Date());
        Team zeroTeam = new Team(identifier, zeroStat, new ArrayList<>(), new ArrayList<>());
        assertEquals(BigDecimal.ZERO, zeroTeam.teamStat().budget());
    }

    @Test
    @DisplayName("Doit vérifier si l'équipe a un budget suffisant")
    void shouldCheckEnoughBudget() {
        Price price = new Price(BigDecimal.valueOf(5000.0));
        assertTrue(team.hasEnoughBudget(price));
    }

    @Test
    @DisplayName("Doit retourner faux lorsque le budget est insuffisant")
    void shouldReturnFalseWhenBudgetInsufficient() {
        Price price = new Price(BigDecimal.valueOf(15000.0));
        assertFalse(team.hasEnoughBudget(price));
    }

    @Test
    @DisplayName("Doit calculer le budget disponible après dépense")
    void shouldCalculateAvailableBudget() {
        Price price = new Price(BigDecimal.valueOf(2000.0));
        BigDecimal available = team.getAvailableBudget(price);

        assertEquals(BigDecimal.valueOf(8000.0), available);
    }

    @Test
    @DisplayName("Doit ajouter un joueur et réduire le budget")
    void shouldAddPlayerAndReduceBudget() {
        PlayerId playerId = new PlayerId(100L);
        Price price = new Price(BigDecimal.valueOf(1000.0));
        Transfer transfer = new Transfer(
            new TransferId(1L),
            playerId,
            new TeamId(2L),
            teamId,
            price,
            new Date()
        );

        Team updatedTeam = team.addPlayer(playerId, price, transfer);

        // Équipe originale inchangée
        assertEquals(BigDecimal.valueOf(10000.0), team.teamStat().budget());
        assertEquals(0, team.playerIds().size());

        // L'équipe mise à jour contient le joueur et un budget réduit
        assertEquals(BigDecimal.valueOf(9000.0), updatedTeam.teamStat().budget());
        assertEquals(1, updatedTeam.playerIds().size());
        assertTrue(updatedTeam.playerIds().contains(playerId));
        assertEquals(1, updatedTeam.transferHistory().size());
        assertEquals(team.teamStat().version() + 1, updatedTeam.teamStat().version());
    }

    @Test
    @DisplayName("Doit lever une exception lors de l'ajout d'un joueur sans budget suffisant")
    void shouldThrowExceptionWhenInsufficitBudgetForPlayer() {
        PlayerId playerId = new PlayerId(100L);
        Price price = new Price(BigDecimal.valueOf(15000.0));
        Transfer transfer = new Transfer(
            new TransferId(1L),
            playerId,
            new TeamId(2L),
            teamId,
            price,
            new Date()
        );

        assertThrows(InsufficientBudgetException.class, () ->
            team.addPlayer(playerId, price, transfer)
        );
    }

    @Test
    @DisplayName("Doit retirer un joueur et augmenter le budget")
    void shouldRemovePlayerAndIncreaseBudget() {
        // Ajouter d'abord un joueur
        PlayerId playerId = new PlayerId(100L);
        Price price = new Price(BigDecimal.valueOf(1000.0));
        Transfer addTransfer = new Transfer(
            new TransferId(1L),
            playerId,
            new TeamId(2L),
            teamId,
            price,
            new Date()
        );
        Team teamWithPlayer = team.addPlayer(playerId, price, addTransfer);

        // Puis retirer le joueur
        Transfer removeTransfer = new Transfer(
            new TransferId(2L),
            playerId,
            teamId,
            new TeamId(2L),
            price,
            new Date()
        );
        Team updatedTeam = teamWithPlayer.removePlayer(playerId, price, removeTransfer);

        // Budget rétabli
        assertEquals(BigDecimal.valueOf(10000.0), updatedTeam.teamStat().budget());
        assertEquals(0, updatedTeam.playerIds().size());
        assertEquals(2, updatedTeam.transferHistory().size());
        assertEquals(team.teamStat().version() + 2, updatedTeam.teamStat().version());
    }

    @Test
    @DisplayName("Doit mettre à jour lastUpdate lors du recrutement")
    void shouldUpdateLastUpdateWhenRecruiting() throws InterruptedException {
        PlayerId playerId = new PlayerId(100L);
        Price price = new Price(BigDecimal.valueOf(1000.0));
        Transfer transfer = new Transfer(
            new TransferId(1L),
            playerId,
            new TeamId(2L),
            teamId,
            price,
            new Date()
        );

        Date initialLastUpdate = team.teamStat().lastUpdate();
        Thread.sleep(5);

        Team updatedTeam = team.addPlayer(playerId, price, transfer);

        assertTrue(updatedTeam.teamStat().lastUpdate().after(initialLastUpdate));
        assertEquals(team.teamStat().creation(), updatedTeam.teamStat().creation());
    }

    @Test
    @DisplayName("Doit conserver l'historique des transferts")
    void shouldMaintainTransferHistory() {
        PlayerId playerId = new PlayerId(100L);
        Price price = new Price(BigDecimal.valueOf(1000.0));
        Transfer transfer = new Transfer(
            new TransferId(1L),
            playerId,
            new TeamId(2L),
            teamId,
            price,
            new Date()
        );

        Team updatedTeam = team.addPlayer(playerId, price, transfer);

        assertEquals(1, updatedTeam.transferHistory().size());
        assertEquals(transfer, updatedTeam.transferHistory().get(0));
    }

    @Test
    @DisplayName("Doit être immuable - l'ajout d'un joueur renvoie une nouvelle instance")
    void shouldReturnNewInstanceWhenAddingPlayer() {
        PlayerId playerId = new PlayerId(100L);
        Price price = new Price(BigDecimal.valueOf(1000.0));
        Transfer transfer = new Transfer(
            new TransferId(1L),
            playerId,
            new TeamId(2L),
            teamId,
            price,
            new Date()
        );

        Team updatedTeam = team.addPlayer(playerId, price, transfer);

        assertNotSame(team, updatedTeam);
    }

    @Test
    @DisplayName("Doit ajouter plusieurs joueurs")
    void shouldAddMultiplePlayers() {
        Price price1 = new Price(BigDecimal.valueOf(1000.0));
        Price price2 = new Price(BigDecimal.valueOf(2000.0));
        PlayerId playerId1 = new PlayerId(100L);
        PlayerId playerId2 = new PlayerId(101L);

        Transfer transfer1 = new Transfer(new TransferId(1L), playerId1, new TeamId(2L), teamId, price1, new Date());
        Transfer transfer2 = new Transfer(new TransferId(2L), playerId2, new TeamId(2L), teamId, price2, new Date());

        Team team1 = team.addPlayer(playerId1, price1, transfer1);
        Team team2 = team1.addPlayer(playerId2, price2, transfer2);

        assertEquals(2, team2.playerIds().size());
        assertEquals(BigDecimal.valueOf(7000.0), team2.teamStat().budget());
        assertEquals(2, team2.transferHistory().size());
    }
}

