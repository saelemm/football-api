package errors;

import Errors.DomainException;
import Errors.InsufficientBudgetException;
import Errors.TransferNotAllowedException;
import Errors.PlayerNotFoundException;
import Errors.TeamNotFoundException;
import Errors.DuplicateTeamException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests des exceptions du domaine")
class DomainExceptionsTest {

    @Test
    @DisplayName("Doit lever InsufficientBudgetException avec un message")
    void shouldCreateInsufficientBudgetException() {
        String message = "Team does not have enough budget";
        InsufficientBudgetException exception = new InsufficientBudgetException(message);

        assertEquals(message, exception.getMessage());
        assertInstanceOf(DomainException.class, exception);
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    @DisplayName("Doit lever TransferNotAllowedException avec un message")
    void shouldCreateTransferNotAllowedException() {
        String message = "Player cannot be transferred";
        TransferNotAllowedException exception = new TransferNotAllowedException(message);

        assertEquals(message, exception.getMessage());
        assertInstanceOf(DomainException.class, exception);
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    @DisplayName("Doit lever PlayerNotFoundException avec un message")
    void shouldCreatePlayerNotFoundException() {
        String message = "Player not found";
        PlayerNotFoundException exception = new PlayerNotFoundException(message);

        assertEquals(message, exception.getMessage());
        assertInstanceOf(DomainException.class, exception);
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    @DisplayName("Doit lever TeamNotFoundException avec un message")
    void shouldCreateTeamNotFoundException() {
        String message = "Team not found";
        TeamNotFoundException exception = new TeamNotFoundException(message);

        assertEquals(message, exception.getMessage());
        assertInstanceOf(DomainException.class, exception);
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    @DisplayName("Doit lever DuplicateTeamException avec un message")
    void shouldCreateDuplicateTeamException() {
        String message = "Team with this name already exists";
        DuplicateTeamException exception = new DuplicateTeamException(message);

        assertEquals(message, exception.getMessage());
        assertInstanceOf(DomainException.class, exception);
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    @DisplayName("Doit lever interceptable comme DomainException")
    void shouldBeCatchableAsDomainException() {
        assertThrows(DomainException.class, () -> {
            throw new InsufficientBudgetException("Budget error");
        });
    }

    @Test
    @DisplayName("Doit lever interceptable comme RuntimeException")
    void shouldBeCatchableAsRuntimeException() {
        assertThrows(RuntimeException.class, () -> {
            throw new TransferNotAllowedException("Transfer error");
        });
    }
}

