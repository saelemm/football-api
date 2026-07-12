package validator;

import Validator.EnumValidator;
import entity.PositionEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de EnumValidator")
class EnumValidatorTest {

    @Test
    @DisplayName("Doit retourner l'enum correspondant sans tenir compte de la casse")
    void shouldReturnEnumIgnoringCase() {
        PositionEnum result = EnumValidator.fromString(PositionEnum.class, "st");
        assertEquals(PositionEnum.ST, result);
    }

    @Test
    @DisplayName("Doit lever une exception pour une valeur nulle")
    void shouldThrowExceptionWhenValueIsNull() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> EnumValidator.fromString(PositionEnum.class, null)
        );

        assertEquals("Value cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Doit lever une exception pour une valeur inconnue")
    void shouldThrowExceptionWhenValueIsUnknown() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> EnumValidator.fromString(PositionEnum.class, "unknown")
        );

        assertTrue(exception.getMessage().contains("Valeur inconnue 'unknown'"));
        assertTrue(exception.getMessage().contains("PositionEnum"));
    }
}

