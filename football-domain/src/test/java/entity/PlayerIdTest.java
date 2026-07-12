package entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de l'objet valeur PlayerId")
class PlayerIdTest {

    @Test
    @DisplayName("Doit créer un PlayerId avec une valeur Long valide")
    void shouldCreatePlayerId() {
        PlayerId playerId = new PlayerId(1L);
        assertEquals(1L, playerId.value());
    }

    @Test
    @DisplayName("Doit lever une exception lors de la création d'un PlayerId avec une valeur nulle")
    void shouldThrowExceptionWhenValueIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new PlayerId(null));
    }

    @Test
    @DisplayName("Doit être égaux lorsque les valeurs sont égales")
    void shouldBeEqualWhenValuesAreEqual() {
        PlayerId playerId1 = new PlayerId(1L);
        PlayerId playerId2 = new PlayerId(1L);
        assertEquals(playerId1, playerId2);
    }

    @Test
    @DisplayName("ne Doit pas être égaux lorsque les valeurs sont différentes")
    void shouldNotBeEqualWhenValuesDifferent() {
        PlayerId playerId1 = new PlayerId(1L);
        PlayerId playerId2 = new PlayerId(2L);
        assertNotEquals(playerId1, playerId2);
    }

    @Test
    @DisplayName("Doit avoir le même hashCode lorsque les valeurs sont égales")
    void shouldHaveSameHashCodeWhenValuesEqual() {
        PlayerId playerId1 = new PlayerId(1L);
        PlayerId playerId2 = new PlayerId(1L);
        assertEquals(playerId1.hashCode(), playerId2.hashCode());
    }
}

