package integration;

import entity.*;
import Errors.InsufficientBudgetException;
import Errors.TransferNotAllowedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests d'intégration du domaine")
class DomainIntegrationTest {

    private static final long SOURCE_TEAM_ID_VALUE = 1L;
    private static final long TARGET_TEAM_ID_VALUE = 2L;
    private static final String SOURCE_TEAM_NAME = "Real Madrid";
    private static final String SOURCE_TEAM_ACRONYM = "RM";
    private static final String TARGET_TEAM_NAME = "PSG";
    private static final String TARGET_TEAM_ACRONYM = "MC";
    private static final BigDecimal SOURCE_TEAM_BUDGET = BigDecimal.valueOf(50000.0);
    private static final BigDecimal TARGET_TEAM_BUDGET = BigDecimal.valueOf(60000.0);

    private static final long MAIN_PLAYER_ID_VALUE = 100L;
    private static final String MAIN_PLAYER_FIRST_NAME = "Cristiano";
    private static final String MAIN_PLAYER_LAST_NAME = "Ronaldo";
    private static final String MAIN_PLAYER_ACRONYM = "CR7";
    private static final float MAIN_PLAYER_PERFORMANCE = 9.5f;
    private static final BigDecimal MAIN_PLAYER_PRICE = BigDecimal.valueOf(50000.0);

    private static final long NON_TITULAIRE_PLAYER_ID_VALUE = 101L;
    private static final String NON_TITULAIRE_PLAYER_FIRST_NAME = "Kylian";
    private static final String NON_TITULAIRE_PLAYER_LAST_NAME = "Mbappe";
    private static final String NON_TITULAIRE_PLAYER_ACRONYM = "MOBUTU";
    private static final float NON_TITULAIRE_PLAYER_PERFORMANCE = 7.5f;
    private static final BigDecimal NON_TITULAIRE_PLAYER_PRICE = BigDecimal.valueOf(5000.0);

    private static final long SCENARIO_PLAYER_ID_VALUE = 102L;
    private static final String SCENARIO_PLAYER_FIRST_NAME = "Oussmane";
    private static final String SCENARIO_PLAYER_LAST_NAME = "Dembele";
    private static final String SCENARIO_PLAYER_ACRONYM = "BALLON DOR";
    private static final float SCENARIO_PLAYER_PERFORMANCE = 8.5f;
    private static final BigDecimal SCENARIO_PLAYER_PRICE = BigDecimal.valueOf(10000.0);

    private Team sourceTeam;
    private Team targetTeam;
    private Player player;
    private PlayerId playerId;
    private TeamId sourceTeamId;
    private TeamId targetTeamId;
    private Player nonTitulairePlayer;
    private Player scenarioPlayer;
    private PlayerId scenarioPlayerId;
    private Price scenarioPlayerPrice;

    @BeforeEach
    void setUp() {
        sourceTeamId = new TeamId(SOURCE_TEAM_ID_VALUE);
        targetTeamId = new TeamId(TARGET_TEAM_ID_VALUE);

        sourceTeam = createTeam(sourceTeamId, SOURCE_TEAM_NAME, SOURCE_TEAM_ACRONYM, SOURCE_TEAM_BUDGET);
        targetTeam = createTeam(targetTeamId, TARGET_TEAM_NAME, TARGET_TEAM_ACRONYM, TARGET_TEAM_BUDGET);

        playerId = new PlayerId(MAIN_PLAYER_ID_VALUE);
        player = createPlayer(playerId, MAIN_PLAYER_FIRST_NAME, MAIN_PLAYER_LAST_NAME, MAIN_PLAYER_ACRONYM, sourceTeamId,
            PositionEnum.ST, MAIN_PLAYER_PERFORMANCE, MAIN_PLAYER_PRICE, true);

        PlayerId nonTitulairePlayerId = new PlayerId(NON_TITULAIRE_PLAYER_ID_VALUE);
        nonTitulairePlayer = createPlayer(nonTitulairePlayerId, NON_TITULAIRE_PLAYER_FIRST_NAME, NON_TITULAIRE_PLAYER_LAST_NAME,
            NON_TITULAIRE_PLAYER_ACRONYM, sourceTeamId, PositionEnum.CM, NON_TITULAIRE_PLAYER_PERFORMANCE,
            NON_TITULAIRE_PLAYER_PRICE, false);

        scenarioPlayerId = new PlayerId(SCENARIO_PLAYER_ID_VALUE);
        scenarioPlayerPrice = new Price(SCENARIO_PLAYER_PRICE);
        scenarioPlayer = createPlayer(scenarioPlayerId, SCENARIO_PLAYER_FIRST_NAME, SCENARIO_PLAYER_LAST_NAME,
            SCENARIO_PLAYER_ACRONYM, sourceTeamId, PositionEnum.LW, SCENARIO_PLAYER_PERFORMANCE,
            SCENARIO_PLAYER_PRICE, false);
    }

    @Test
    @DisplayName("Doit transférer avec succès un joueur non titulaire")
    void shouldSuccessfullyTransferNonTitulaire() {
        // Transférer le joueur
        assertTrue(nonTitulairePlayer.canBeTransferred());
        Player transferredPlayer = nonTitulairePlayer.transferTo(targetTeamId);

        // Vérifier le transfert
        assertEquals(targetTeamId, transferredPlayer.identifier().teamId());
        assertEquals(sourceTeamId, nonTitulairePlayer.identifier().teamId());
    }

    @Test
    @DisplayName("Doit échouer à transférer un joueur titulaire")
    void shouldFailToTransferTitulaire() {
        // Le joueur est créé comme titulaire (true)
        assertFalse(player.canBeTransferred());
        assertThrows(TransferNotAllowedException.class, () ->
            player.transferTo(targetTeamId)
        );
    }

    @Test
    @DisplayName("Doit gérer les contraintes de budget de l'équipe")
    void shouldHandleTeamBudgetConstraints() {
        // Créer un transfert coûteux
        Price expensivePrice = new Price(BigDecimal.valueOf(100000.0));
        Transfer transfer = new Transfer(
            new TransferId(1L),
            playerId,
            sourceTeamId,
            targetTeamId,
            expensivePrice,
            new Date()
        );

        // L'équipe cible n'a pas assez de budget
        assertFalse(targetTeam.hasEnoughBudget(expensivePrice));

        assertThrows(InsufficientBudgetException.class, () ->
            targetTeam.addPlayer(playerId, expensivePrice, transfer)
        );
    }

    @Test
    @DisplayName("Doit terminer un scénario complet de transfert")
    void shouldCompleteFullTransferScenario() {
        // Ajouter d'abord le joueur à l'équipe source
        Transfer addTransfer = new Transfer(
            new TransferId(1L),
            scenarioPlayerId,
            null, // Recrutement
            sourceTeamId,
            scenarioPlayerPrice,
            new Date()
        );

        Team sourceTeamWithPlayer = sourceTeam.addPlayer(scenarioPlayerId, scenarioPlayerPrice, addTransfer);

        // Vérifier que le joueur a été ajouté
        assertEquals(1, sourceTeamWithPlayer.playerIds().size());
        assertTrue(sourceTeamWithPlayer.playerIds().contains(scenarioPlayerId));
        assertEquals(BigDecimal.valueOf(40000.0), sourceTeamWithPlayer.teamStat().budget());

        // Transférer le joueur vers l'équipe cible
        assertTrue(scenarioPlayer.canBeTransferred());
        Player transferredPlayer = scenarioPlayer.transferTo(targetTeamId);
        assertEquals(targetTeamId, transferredPlayer.identifier().teamId());

        // Mettre à jour les équipes avec le transfert
        Transfer moveTransfer = new Transfer(
            new TransferId(2L),
            scenarioPlayerId,
            sourceTeamId,
            targetTeamId,
            scenarioPlayerPrice,
            new Date()
        );

        Team sourceTeamAfterTransfer = sourceTeamWithPlayer.removePlayer(scenarioPlayerId, scenarioPlayerPrice, moveTransfer);
        Team targetTeamAfterTransfer = targetTeam.addPlayer(scenarioPlayerId, scenarioPlayerPrice, moveTransfer);

        // Vérifier l'état final
        assertEquals(0, sourceTeamAfterTransfer.playerIds().size());
        assertEquals(BigDecimal.valueOf(50000.0), sourceTeamAfterTransfer.teamStat().budget()); // Rétabli

        assertEquals(1, targetTeamAfterTransfer.playerIds().size());
        assertTrue(targetTeamAfterTransfer.playerIds().contains(scenarioPlayerId));
        assertEquals(BigDecimal.valueOf(50000.0), targetTeamAfterTransfer.teamStat().budget()); // Réduit

        // Vérifier l'historique des transferts
        assertEquals(2, sourceTeamAfterTransfer.transferHistory().size());
        assertEquals(1, targetTeamAfterTransfer.transferHistory().size());
    }

    @Test
    @DisplayName("Doit gérer les mises à jour de performance du joueur")
    void shouldHandlePlayerPerformanceUpdates() {
        Note oldNote = player.stats().performanceNote();
        Note newNote = new Note(8.5f);

        assertEquals(oldNote, player.stats().performanceNote());

        Player updatedPlayer = player.updatePerformance(newNote);

        // L'original reste inchangé
        assertEquals(oldNote, player.stats().performanceNote());

        // Le joueur mis à jour a une nouvelle performance
        assertEquals(newNote, updatedPlayer.stats().performanceNote());
    }

    @Test
    @DisplayName("Doit maintenir l'intégrité des données à travers plusieurs opérations")
    void shouldMaintainDataIntegrity() {
        // Créer plusieurs joueurs
        PlayerId playerId1 = new PlayerId(201L);
        PlayerId playerId2 = new PlayerId(202L);
        PlayerId playerId3 = new PlayerId(203L);

        Price price1 = new Price(BigDecimal.valueOf(1000.0));
        Price price2 = new Price(BigDecimal.valueOf(2000.0));
        Price price3 = new Price(BigDecimal.valueOf(1500.0));

        // Ajouter les joueurs à l'équipe cible
        Transfer transfer1 = new Transfer(new TransferId(101L), playerId1, sourceTeamId, targetTeamId, price1, new Date());
        Transfer transfer2 = new Transfer(new TransferId(102L), playerId2, sourceTeamId, targetTeamId, price2, new Date());
        Transfer transfer3 = new Transfer(new TransferId(103L), playerId3, sourceTeamId, targetTeamId, price3, new Date());

        Team team1 = targetTeam.addPlayer(playerId1, price1, transfer1);
        Team team2 = team1.addPlayer(playerId2, price2, transfer2);
        Team team3 = team2.addPlayer(playerId3, price3, transfer3);

        // Vérifier que tous les joueurs ont été ajoutés
        assertEquals(3, team3.playerIds().size());

        // Vérifier le calcul du budget
        BigDecimal totalSpent = price1.value().add(price2.value()).add(price3.value());
        assertEquals(BigDecimal.valueOf(60000.0).subtract(totalSpent), team3.teamStat().budget());

        // Vérifier l'historique des transferts
        assertEquals(3, team3.transferHistory().size());
    }

    private Team createTeam(TeamId teamId, String name, String acronym, BigDecimal budget) {
        TeamIdentifier identifier = new TeamIdentifier(teamId, name, acronym);
        TeamStat stat = new TeamStat(budget, new Date(), new Date());
        return new Team(identifier, stat, new ArrayList<>(), new ArrayList<>());
    }

    private Player createPlayer(
        PlayerId id,
        String firstName,
        String lastName,
        String acronym,
        TeamId teamId,
        PositionEnum position,
        float performance,
        BigDecimal marketPrice,
        boolean titulaire
    ) {
        PlayerIdentifier identifier = new PlayerIdentifier(id, firstName, lastName, acronym, teamId);
        PlayerStat stat = new PlayerStat(position, new Note(performance), new Price(marketPrice), titulaire);
        PlayerVersion version = new PlayerVersion(0, new Date(), new Date());
        return new Player(identifier, stat, version);
    }
}

