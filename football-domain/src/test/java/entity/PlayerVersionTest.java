package entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Tests de PlayerVersion")
class PlayerVersionTest {

    @Test
    @DisplayName("doit incrementer la version")
    void shouldIncrementVersion() {
        Date now = new Date();
        PlayerVersion version = new PlayerVersion(2, now, now);

        PlayerVersion incremented = version.incrementVersion();

        assertEquals(3, incremented.version());
    }

    @Test
    @DisplayName("doit conserver createdAt lors de l'incrementation")
    void shouldKeepCreatedAtWhenIncrementing() {
        Date createdAt = new Date();
        PlayerVersion version = new PlayerVersion(2, createdAt, createdAt);

        PlayerVersion incremented = version.incrementVersion();

        assertEquals(createdAt, incremented.createdAt());
    }

    @Test
    @DisplayName("doit mettre a jour updatedAt lors de l'incrementation")
    void shouldUpdateUpdatedAtWhenIncrementing() throws InterruptedException {
        Date createdAt = new Date();
        Date updatedAt = new Date();
        PlayerVersion version = new PlayerVersion(2, createdAt, updatedAt);

        Thread.sleep(5);
        PlayerVersion incremented = version.incrementVersion();

        assertTrue(incremented.updatedAt().after(updatedAt));
    }

    @Test
    @DisplayName("doit retourner une nouvelle instance")
    void shouldReturnNewInstance() {
        Date now = new Date();
        PlayerVersion version = new PlayerVersion(2, now, now);

        PlayerVersion incremented = version.incrementVersion();

        assertNotSame(version, incremented);
    }
}

