package entity;

import Errors.TransferNotAllowedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de l'entité Transfer")
class TransferTest {

    private final TransferId transferId = new TransferId(1L);
    private final PlayerId playerId = new PlayerId(100L);
    private final TeamId sourceTeamId = new TeamId(1L);
    private final TeamId targetTeamId = new TeamId(2L);
    private final Price price = new Price(BigDecimal.valueOf(1000.0));
    private final Date date = new Date();

    @Test
    @DisplayName("Doit créer un Transfer valide avec tous les champs requis")
    void shouldCreateValidTransfer() {
        Transfer transfer = new Transfer(transferId, playerId, sourceTeamId, targetTeamId, price, date);

        assertEquals(transferId, transfer.transferId());
        assertEquals(playerId, transfer.playerId());
        assertEquals(sourceTeamId, transfer.sourceTeamId());
        assertEquals(targetTeamId, transfer.targetTeamId());
        assertEquals(price, transfer.transferPrice());
        assertEquals(date, transfer.transferDate());
    }

    @Test
    @DisplayName("Doit lever une exception lorsque playerId est nul")
    void shouldThrowExceptionWhenPlayerIdIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
            new Transfer(transferId, null, sourceTeamId, targetTeamId, price, date)
        );
    }

    @Test
    @DisplayName("Doit lever une exception lorsque targetTeamId est nul")
    void shouldThrowExceptionWhenTargetTeamIdIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
            new Transfer(transferId, playerId, sourceTeamId, null, price, date)
        );
    }

    @Test
    @DisplayName("Doit lever une exception lorsque transferPrice est nul")
    void shouldThrowExceptionWhenPriceIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
            new Transfer(transferId, playerId, sourceTeamId, targetTeamId, null, date)
        );
    }

    @Test
    @DisplayName("Doit lever une exception lorsque sourceTeamId est égal à targetTeamId")
    void shouldThrowExceptionWhenTeamsAreSame() {
        TeamId sameTeam = new TeamId(1L);
        TeamId otherTeam = new TeamId(1L);

        assertThrows(TransferNotAllowedException.class, () ->
            new Transfer(transferId, playerId, sameTeam, otherTeam, price, date)
        );
    }

    @Test
    @DisplayName("Doit autoriser un Transfer avec sourceTeamId nul (cas de recrutement)")
    void shouldAllowNullSourceTeamId() {
        Transfer transfer = new Transfer(transferId, playerId, null, targetTeamId, price, date);
        assertNull(transfer.sourceTeamId());
    }

    @Test
    @DisplayName("Doit être égaux lorsque tous les champs sont égaux")
    void shouldBeEqualWhenFieldsEqual() {
        Transfer transfer1 = new Transfer(transferId, playerId, sourceTeamId, targetTeamId, price, date);
        Transfer transfer2 = new Transfer(transferId, playerId, sourceTeamId, targetTeamId, price, date);

        assertEquals(transfer1, transfer2);
    }

    @Test
    @DisplayName("Doit avoir le même hashCode lorsque tous les champs sont égaux")
    void shouldHaveSameHashCodeWhenFieldsEqual() {
        Transfer transfer1 = new Transfer(transferId, playerId, sourceTeamId, targetTeamId, price, date);
        Transfer transfer2 = new Transfer(transferId, playerId, sourceTeamId, targetTeamId, price, date);

        assertEquals(transfer1.hashCode(), transfer2.hashCode());
    }
}

