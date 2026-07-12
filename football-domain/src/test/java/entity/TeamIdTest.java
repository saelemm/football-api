package entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de l'objet valeur TeamId")
class TeamIdTest {

    @Test
    @DisplayName("Doit créer un TeamId avec une valeur Long valide")
    void shouldCreateTeamId() {
        TeamId teamId = new TeamId(1L);
        assertEquals(1L, teamId.value());
    }

    @Test
    @DisplayName("Doit lever une exception lors de la création d'un TeamId avec une valeur nulle")
    void shouldThrowExceptionWhenValueIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new TeamId(null));
    }

    @Test
    @DisplayName("Doit être égaux lorsque les valeurs sont égales")
    void shouldBeEqualWhenValuesAreEqual() {
        TeamId teamId1 = new TeamId(1L);
        TeamId teamId2 = new TeamId(1L);
        assertEquals(teamId1, teamId2);
    }

    @Test
    @DisplayName("Ne doit pas être égaux lorsque les valeurs sont différentes")
    void shouldNotBeEqualWhenValuesDifferent() {
        TeamId teamId1 = new TeamId(1L);
        TeamId teamId2 = new TeamId(2L);
        assertNotEquals(teamId1, teamId2);
    }

    @Test
    @DisplayName("Doit avoir le même hashCode lorsque les valeurs sont égales")
    void shouldHaveSameHashCodeWhenValuesEqual() {
        TeamId teamId1 = new TeamId(1L);
        TeamId teamId2 = new TeamId(1L);
        assertEquals(teamId1.hashCode(), teamId2.hashCode());
    }
}

