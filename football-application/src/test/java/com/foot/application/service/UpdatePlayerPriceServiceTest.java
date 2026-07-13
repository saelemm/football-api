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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import port.IPlayerRepository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires de UpdatePlayerPriceService")
class UpdatePlayerPriceServiceTest {

    @Mock
    private IPlayerRepository playerRepository;

    @InjectMocks
    private UpdatePlayerPriceService service;

    @Test
    @DisplayName("Doit incrementer la version lors de la mise a jour du prix")
    void shouldIncrementVersionWhenUpdatingPrice() {
        Date now = new Date();
        Player player = new Player(
            new PlayerIdentifier(new PlayerId(3L), "Kylian", "Mbappe", "KM", new TeamId(1L)),
            new PlayerStat(PositionEnum.ST, new Note(8.5f), new Price(BigDecimal.valueOf(100.0)), false),
            new PlayerVersion(0, now, now)
        );

        when(playerRepository.findById(new PlayerId(3L))).thenReturn(Optional.of(player));

        service.execute(new PlayerId(3L), BigDecimal.valueOf(10.0));

        ArgumentCaptor<Player> playerCaptor = ArgumentCaptor.forClass(Player.class);
        verify(playerRepository).save(playerCaptor.capture());

        Player savedPlayer = playerCaptor.getValue();
        assertEquals(1, savedPlayer.version().version());
        assertEquals(BigDecimal.valueOf(110.0), savedPlayer.stats().marketPrice().value());
    }

    @Test
    @DisplayName("Doit lever une erreur si le joueur n'existe pas")
    void shouldThrowWhenPlayerNotFound() {
        when(playerRepository.findById(new PlayerId(999L))).thenReturn(Optional.empty());

        assertThrows(PlayerNotFoundException.class, () -> service.execute(new PlayerId(999L), BigDecimal.ONE));
    }
}

