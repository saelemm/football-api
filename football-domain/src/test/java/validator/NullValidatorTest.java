package validator;

import Validator.NullValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de NullValidator")
class NullValidatorTest {

    @Test
    @DisplayName("ne doit pas lever d'exception pour une valeur non nulle")
    void shouldAllowNonNullValue() {
        assertDoesNotThrow(() ->
            NullValidator.requireNonNull("not null", "Error message")
        );
    }

    @Test
    @DisplayName("ne doit pas lever d'exception pour un objet non nul")
    void shouldAllowNonNullObject() {
        Object obj = new Object();
        assertDoesNotThrow(() ->
            NullValidator.requireNonNull(obj, "Error message")
        );
    }

    @Test
    @DisplayName("doit lever une exception pour une valeur nulle")
    void shouldThrowExceptionForNull() {
        assertThrows(IllegalArgumentException.class, () ->
            NullValidator.requireNonNull(null, "Error message")
        );
    }

    @Test
    @DisplayName("doit lever une exception avec le bon message")
    void shouldThrowExceptionWithCorrectMessage() {
        String errorMessage = "Custom error message";
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> NullValidator.requireNonNull(null, errorMessage)
        );

        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("doit lever une exception pour une chaîne nulle")
    void shouldThrowExceptionForNullString() {
        assertThrows(IllegalArgumentException.class, () ->
            NullValidator.requireNonNull(null, "String cannot be null")
        );
    }

    @Test
    @DisplayName("doit retourner la valeur validée")
    void shouldReturnValidatedValue() {
        String value = "test value";
        String result = NullValidator.requireNonNull(value, "Error");
        assertEquals(value, result);
    }
}

