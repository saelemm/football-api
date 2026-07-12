package entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de l'objet valeur Note")
class NoteTest {

    @Test
    @DisplayName("Doit créer une Note avec une valeur valide entre 0 et 10")
    void shouldCreateNote() {
        Note note = new Note(7.5f);
        assertEquals(7.5f, note.value());
    }

    @Test
    @DisplayName("Doit lever une exception lors de la création d'une Note avec une valeur nulle")
    void shouldThrowExceptionWhenValueIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Note(null));
    }

    @Test
    @DisplayName("Doit lever une exception lors de la création d'une Note avec une valeur inférieure à 0")
    void shouldThrowExceptionWhenValueBelowZero() {
        assertThrows(IllegalArgumentException.class, () -> new Note(-1.0f));
    }

    @Test
    @DisplayName("Doit lever une exception lors de la création d'une Note avec une valeur supérieure à 10")
    void shouldThrowExceptionWhenValueAboveTen() {
        assertThrows(IllegalArgumentException.class, () -> new Note(10.1f));
    }

    @Test
    @DisplayName("Doit autoriser une Note avec la valeur 0")
    void shouldAllowNoteWithZero() {
        Note note = new Note(0.0f);
        assertEquals(0.0f, note.value());
    }

    @Test
    @DisplayName("Doit autoriser une Note avec la valeur 10")
    void shouldAllowNoteWithTen() {
        Note note = new Note(10.0f);
        assertEquals(10.0f, note.value());
    }

    @Test
    @DisplayName("Doit être égaux lorsque les valeurs sont égales")
    void shouldBeEqualWhenValuesAreEqual() {
        Note note1 = new Note(7.5f);
        Note note2 = new Note(7.5f);
        assertEquals(note1, note2);
    }

    @Test
    @DisplayName("Ne doit pas être égaux lorsque les valeurs sont différentes")
    void shouldNotBeEqualWhenValuesDifferent() {
        Note note1 = new Note(7.5f);
        Note note2 = new Note(8.0f);
        assertNotEquals(note1, note2);
    }

    @Test
    @DisplayName("Doit améliorer la note dans la plage valide")
    void shouldImproveNoteWithinRange() {
        Note note = new Note(7.0f);

        Note improved = note.improve(1.5f);

        assertEquals(8.5f, improved.value());
        assertEquals(7.0f, note.value());
    }

    @Test
    @DisplayName("Doit plafonner la note à 10 lors de l'amélioration")
    void shouldCapNoteAtTenWhenImproving() {
        Note note = new Note(9.5f);

        Note improved = note.improve(2.0f);

        assertEquals(10.0f, improved.value());
    }

    @Test
    @DisplayName("Doit diminuer la note dans la plage valide")
    void shouldDeclineNoteWithinRange() {
        Note note = new Note(8.0f);

        Note declined = note.decline(2.5f);

        assertEquals(5.5f, declined.value());
        assertEquals(8.0f, note.value());
    }

    @Test
    @DisplayName("Doit plafonner la note à 0 lors de la diminution")
    void shouldFloorNoteAtZeroWhenDeclining() {
        Note note = new Note(1.0f);

        Note declined = note.decline(3.0f);

        assertEquals(0.0f, declined.value());
    }
}

