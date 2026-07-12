package entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de l'objet valeur TransferId")
class TransferIdTest {

    @Test
    @DisplayName("Doit créer un TransferId avec une valeur Long valide")
    void shouldCreateTransferId() {
        TransferId transferId = new TransferId(1L);
        assertEquals(1L, transferId.value());
    }

    @Test
    @DisplayName("Doit lever une exception lors de la création d'un TransferId avec une valeur nulle")
    void shouldThrowExceptionWhenValueIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new TransferId(null));
    }

    @Test
    @DisplayName("Doit être égaux lorsque les valeurs sont égales")
    void shouldBeEqualWhenValuesAreEqual() {
        TransferId transferId1 = new TransferId(1L);
        TransferId transferId2 = new TransferId(1L);
        assertEquals(transferId1, transferId2);
    }

    @Test
    @DisplayName("ne Doit pas être égaux lorsque les valeurs sont différentes")
    void shouldNotBeEqualWhenValuesDifferent() {
        TransferId transferId1 = new TransferId(1L);
        TransferId transferId2 = new TransferId(2L);
        assertNotEquals(transferId1, transferId2);
    }

    @Test
    @DisplayName("Doit avoir le même hashCode lorsque les valeurs sont égales")
    void shouldHaveSameHashCodeWhenValuesEqual() {
        TransferId transferId1 = new TransferId(1L);
        TransferId transferId2 = new TransferId(1L);
        assertEquals(transferId1.hashCode(), transferId2.hashCode());
    }
}

