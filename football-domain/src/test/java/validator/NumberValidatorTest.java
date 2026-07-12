package validator;

import Validator.NumberValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de NumberValidator")
class NumberValidatorTest {

    @Test
    @DisplayName("ne doit pas lever d'exception pour un BigDecimal positif")
    void shouldAllowPositiveBigDecimal() {
        assertDoesNotThrow(() ->
            NumberValidator.requirePositive(BigDecimal.valueOf(100.0), "Error message")
        );
    }

    @Test
    @DisplayName("doit autoriser un BigDecimal égal à zéro")
    void shouldAllowZeroBigDecimal() {
        assertDoesNotThrow(() ->
            NumberValidator.requirePositive(BigDecimal.ZERO, "Error message")
        );
    }

    @Test
    @DisplayName("doit lever une exception pour un BigDecimal négatif")
    void shouldThrowExceptionForNegativeBigDecimal() {
        assertThrows(IllegalArgumentException.class, () ->
            NumberValidator.requirePositive(BigDecimal.valueOf(-100.0), "Error message")
        );
    }

    @Test
    @DisplayName("doit lever une exception pour un BigDecimal nul")
    void shouldThrowExceptionForNullBigDecimal() {
        assertThrows(IllegalArgumentException.class, () ->
            NumberValidator.requirePositive((BigDecimal) null, "Error message")
        );
    }

    @Test
    @DisplayName("ne doit pas lever d'exception pour un Number positif")
    void shouldAllowPositiveNumber() {
        assertDoesNotThrow(() ->
            NumberValidator.requirePositive(100, "Error message")
        );
    }

    @Test
    @DisplayName("doit autoriser un Number égal à zéro")
    void shouldAllowZeroNumber() {
        assertDoesNotThrow(() ->
            NumberValidator.requirePositive(0, "Error message")
        );
    }

    @Test
    @DisplayName("doit lever une exception pour un Number négatif")
    void shouldThrowExceptionForNegativeNumber() {
        assertThrows(IllegalArgumentException.class, () ->
            NumberValidator.requirePositive(-100, "Error message")
        );
    }

    @Test
    @DisplayName("doit lever une exception pour un Number nul")
    void shouldThrowExceptionForNullNumber() {
        assertThrows(IllegalArgumentException.class, () ->
            NumberValidator.requirePositive((Integer) null, "Error message")
        );
    }

    @Test
    @DisplayName("ne doit pas lever d'exception pour un float dans l'intervalle valide")
    void shouldAllowFloatInRange() {
        assertDoesNotThrow(() ->
            NumberValidator.requireInRange(5.0f, 0.0f, 10.0f, "Error message")
        );
    }

    @Test
    @DisplayName("doit autoriser un float à la borne minimale")
    void shouldAllowFloatAtMin() {
        assertDoesNotThrow(() ->
            NumberValidator.requireInRange(0.0f, 0.0f, 10.0f, "Error message")
        );
    }

    @Test
    @DisplayName("doit autoriser un float à la borne maximale")
    void shouldAllowFloatAtMax() {
        assertDoesNotThrow(() ->
            NumberValidator.requireInRange(10.0f, 0.0f, 10.0f, "Error message")
        );
    }

    @Test
    @DisplayName("doit lever une exception pour un float inférieur au minimum")
    void shouldThrowExceptionForFloatBelowMin() {
        assertThrows(IllegalArgumentException.class, () ->
            NumberValidator.requireInRange(-1.0f, 0.0f, 10.0f, "Error message")
        );
    }

    @Test
    @DisplayName("doit lever une exception pour un float supérieur au maximum")
    void shouldThrowExceptionForFloatAboveMax() {
        assertThrows(IllegalArgumentException.class, () ->
            NumberValidator.requireInRange(11.0f, 0.0f, 10.0f, "Error message")
        );
    }
}

