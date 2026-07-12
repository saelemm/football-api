package entity;

import Errors.TransferNotAllowedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de l'agrégat Player")
class PlayerTest {

    private Player player;
    private PlayerId playerId;
    private TeamId teamId;
    private Note performanceNote;
    private Price marketPrice;

    @BeforeEach
    void setUp() {
        playerId = new PlayerId(1L);
        teamId = new TeamId(1L);
        performanceNote = new Note(7.5f);
        marketPrice = new Price(BigDecimal.valueOf(5000.0));

        PlayerIdentifier identifier = new PlayerIdentifier(playerId, "John", "Doe", "JD", teamId);
        PositionEnum position = PositionEnum.CM;
        PlayerStat stats = new PlayerStat(position, performanceNote, marketPrice, false);
        PlayerVersion version = new PlayerVersion(0, new Date(), new Date());

        player = new Player(identifier, stats, version);
    }

    @Test
    @DisplayName("Doit créer un Player valide")
    void shouldCreatePlayer() {
        assertNotNull(player);
        assertEquals("John", player.identifier().firstName());
        assertEquals("Doe", player.identifier().lastName());
        assertFalse(player.stats().isTitulaire());
    }

    @Test
    @DisplayName("Doit mettre à jour la performance et renvoyer une nouvelle instance de Player")
    void shouldUpdatePerformance() {
        Note newNote = new Note(8.5f);
        Player updatedPlayer = player.updatePerformance(newNote);

        // Joueur original inchangé
        assertEquals(7.5f, player.stats().performanceNote().value());

        // Le nouveau joueur a une note mise à jour
        assertEquals(8.5f, updatedPlayer.stats().performanceNote().value());

        // Instances différentes
        assertNotSame(player, updatedPlayer);
    }

    @Test
    @DisplayName("Doit mettre à jour la performance avec la valeur minimale")
    void shouldUpdatePerformanceWithMinValue() {
        Note newNote = new Note(0.0f);
        Player updatedPlayer = player.updatePerformance(newNote);

        assertEquals(0.0f, updatedPlayer.stats().performanceNote().value());
    }

    @Test
    @DisplayName("Doit mettre à jour la performance avec la valeur maximale")
    void shouldUpdatePerformanceWithMaxValue() {
        Note newNote = new Note(10.0f);
        Player updatedPlayer = player.updatePerformance(newNote);

        assertEquals(10.0f, updatedPlayer.stats().performanceNote().value());
    }

    @Test
    @DisplayName("Doit autoriser le transfert d'un joueur non titulaire")
    void shouldAllowNonTitulairPlayerTransfer() {
        assertTrue(player.canBeTransferred());
    }

    @Test
    @DisplayName("ne Doit pas autoriser le transfert d'un joueur titulaire")
    void shouldNotAllowTitulairPlayerTransfer() {
        PositionEnum position = PositionEnum.ST;
        PlayerStat stats = new PlayerStat(position, performanceNote, marketPrice, true);
        PlayerVersion version = new PlayerVersion(0, new Date(), new Date());
        PlayerIdentifier identifier = new PlayerIdentifier(playerId, "Jane", "Smith", "JS", teamId);

        Player titulairPlayer = new Player(identifier, stats, version);

        assertFalse(titulairPlayer.canBeTransferred());
    }

    @Test
    @DisplayName("Doit transférer un joueur non titulaire vers une nouvelle équipe")
    void shouldTransferNonTitulairPlayer() {
        TeamId newTeamId = new TeamId(2L);
        Player transferredPlayer = player.transferTo(newTeamId);

        // Joueur original inchangé
        assertEquals(1L, player.identifier().teamId().value());

        // Le joueur transféré a une nouvelle équipe
        assertEquals(2L, transferredPlayer.identifier().teamId().value());

        // Instances différentes
        assertNotSame(player, transferredPlayer);
    }

    @Test
    @DisplayName("Doit lever une exception lors du transfert d'un joueur titulaire")
    void shouldThrowExceptionWhenTransferringTitulairPlayer() {
        PositionEnum position = PositionEnum.ST;
        PlayerStat stats = new PlayerStat(position, performanceNote, marketPrice, true);
        PlayerVersion version = new PlayerVersion(0, new Date(), new Date());
        PlayerIdentifier identifier = new PlayerIdentifier(playerId, "Jane", "Smith", "JS", teamId);

        Player titulairPlayer = new Player(identifier, stats, version);
        TeamId newTeamId = new TeamId(2L);

        assertThrows(TransferNotAllowedException.class, () -> titulairPlayer.transferTo(newTeamId));
    }

    @Test
    @DisplayName("Doit conserver les autres statistiques après transfert")
    void shouldPreserveOtherStatsAfterTransfer() {
        TeamId newTeamId = new TeamId(2L);
        Player transferredPlayer = player.transferTo(newTeamId);

        // Statistiques conservées
        assertEquals(player.stats().position(), transferredPlayer.stats().position());
        assertEquals(player.stats().performanceNote(), transferredPlayer.stats().performanceNote());
        assertEquals(player.stats().marketPrice(), transferredPlayer.stats().marketPrice());
        assertEquals(player.stats().isTitulaire(), transferredPlayer.stats().isTitulaire());
    }

    @Test
    @DisplayName("Doit conserver les informations personnelles après transfert")
    void shouldPreservePersonalInfoAfterTransfer() {
        TeamId newTeamId = new TeamId(2L);
        Player transferredPlayer = player.transferTo(newTeamId);

        // Informations personnelles conservées
        assertEquals(player.identifier().firstName(), transferredPlayer.identifier().firstName());
        assertEquals(player.identifier().lastName(), transferredPlayer.identifier().lastName());
        assertEquals(player.identifier().acronym(), transferredPlayer.identifier().acronym());
    }
}

