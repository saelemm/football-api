package com.foot.application.service;

import Errors.PlayerNotFoundException;
import entity.Note;
import entity.Player;
import entity.PlayerId;
import entity.PlayerIdentifier;
import entity.PlayerStat;
import entity.PlayerVersion;
import entity.PositionEnum;
import entity.Price;
import entity.TeamId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import port.IPlayerRepository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires de GetPlayerDetailsService")
class GetPlayerDetailsServiceTest {

    @Mock
    private IPlayerRepository playerRepository;

    @InjectMocks
    private GetPlayerDetailsService service;

    @Test
    @DisplayName("Doit retourner le joueur quand il existe")
    void shouldReturnPlayerWhenExists() {
        PlayerId playerId = new PlayerId(7L);
        Date now = new Date();
        Player player = new Player(
            new PlayerIdentifier(playerId, "Leo", "Messi", "LM", new TeamId(3L)),
            new PlayerStat(PositionEnum.RW, new Note(9.5f), new Price(BigDecimal.valueOf(1000.0)), true),
            new PlayerVersion(0, now, now)
        );

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));

        Player result = service.execute(playerId);

        assertEquals(player, result);
    }

    @Test
    @DisplayName("Doit lever une erreur quand le joueur n'existe pas")
    void shouldThrowWhenPlayerDoesNotExist() {
        PlayerId playerId = new PlayerId(404L);
        when(playerRepository.findById(playerId)).thenReturn(Optional.empty());

        PlayerNotFoundException exception = assertThrows(PlayerNotFoundException.class,
            () -> service.execute(playerId));

        assertEquals("Joueur introuvable : 404", exception.getMessage());
    }
}

