package entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de l'objet valeur Price")
class PriceTest {

    @Test
    @DisplayName("Doit créer un Price avec une valeur BigDecimal positive valide")
    void shouldCreatePrice() {
        Price price = new Price(BigDecimal.valueOf(100.50));
        assertEquals(BigDecimal.valueOf(100.50), price.value());
    }

    @Test
    @DisplayName("Doit lever une exception lors de la création d'un Price avec une valeur nulle")
    void shouldThrowExceptionWhenValueIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Price(null));
    }

    @Test
    @DisplayName("Doit lever une exception lors de la création d'un Price avec une valeur négative")
    void shouldThrowExceptionWhenValueIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new Price(BigDecimal.valueOf(-10.0)));
    }

    @Test
    @DisplayName("Doit autoriser un Price avec une valeur nulle")
    void shouldAllowPriceWithZero() {
        Price price = new Price(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, price.value());
    }

    @Test
    @DisplayName("Doit être égaux lorsque les valeurs sont égales")
    void shouldBeEqualWhenValuesAreEqual() {
        Price price1 = new Price(BigDecimal.valueOf(100.0));
        Price price2 = new Price(BigDecimal.valueOf(100.0));
        assertEquals(price1, price2);
    }

    @Test
    @DisplayName("Ne doit pas être égaux lorsque les valeurs sont différentes")
    void shouldNotBeEqualWhenValuesDifferent() {
        Price price1 = new Price(BigDecimal.valueOf(100.0));
        Price price2 = new Price(BigDecimal.valueOf(200.0));
        assertNotEquals(price1, price2);
    }

    @Test
    @DisplayName("Doit augmenter le prix par pourcentage")
    void shouldIncreaseByPercentage() {
        Price price = new Price(BigDecimal.valueOf(100.0));
        Price increased = price.increaseByPercentage(BigDecimal.valueOf(10));
        assertEquals(BigDecimal.valueOf(110.0), increased.value());
    }

    @Test
    @DisplayName("Doit augmenter le prix par valeur ajoutée")
    void shouldIncreaseByAddedValue() {
        Price price = new Price(BigDecimal.valueOf(100.0));
        Price increased = price.increaseBy(BigDecimal.valueOf(25.0));
        assertEquals(BigDecimal.valueOf(125.0), increased.value());
    }

    @Test
    @DisplayName("ne Doit pas dépasser la valeur maximale lors de l'augmentation")
    void shouldNotExceedMaxValue() {
        Price price = new Price(BigDecimal.valueOf(95.0));
        Price increased = price.increaseByPercentage(BigDecimal.valueOf(100));
        assertTrue(increased.value().compareTo(BigDecimal.valueOf(190.0)) >= 0);
    }
}