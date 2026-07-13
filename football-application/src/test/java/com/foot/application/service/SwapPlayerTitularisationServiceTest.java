package com.foot.application.service;

import Errors.PlayerNotFoundException;
import Errors.TitularisationNotAllowedException;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires de SwapPlayerTitularisationService")
class SwapPlayerTitularisationServiceTest {

    @Mock
    private IPlayerRepository playerRepository;

    @InjectMocks
    private SwapPlayerTitularisationService service;

    @Test
    @DisplayName("Doit échanger la titularisation entre deux joueurs de la même équipe")
    void shouldSwapTitularisationBetweenPlayersFromSameTeam() {
        TeamId teamId = new TeamId(1L);
        Player titulairePlayer = player(10L, teamId, true, "Starter");
        Player replacementPlayer = player(11L, teamId, false, "Bench");

        when(playerRepository.findByTeamId(teamId)).thenReturn(List.of(titulairePlayer, replacementPlayer));

        service.execute(teamId, new PlayerId(10L), new PlayerId(11L));

        ArgumentCaptor<Player> savedPlayers = ArgumentCaptor.forClass(Player.class);
        verify(playerRepository, times(2)).save(savedPlayers.capture());

        assertEquals(2, savedPlayers.getAllValues().size());
        Player savedFormerStarter = savedPlayers.getAllValues().get(0);
        Player savedNewStarter = savedPlayers.getAllValues().get(1);

        assertFalse(savedFormerStarter.stats().isTitulaire());
        assertTrue(savedNewStarter.stats().isTitulaire());
        assertEquals(teamId, savedFormerStarter.identifier().teamId());
        assertEquals(teamId, savedNewStarter.identifier().teamId());
    }

    @Test
    @DisplayName("Doit lever une erreur si le joueur titulaire est introuvable dans l'équipe")
    void shouldThrowWhenTitulairePlayerIsMissingFromTeam() {
        TeamId teamId = new TeamId(1L);
        when(playerRepository.findByTeamId(teamId)).thenReturn(List.of(player(11L, teamId, false, "Bench")));

        PlayerNotFoundException exception = assertThrows(PlayerNotFoundException.class,
            () -> service.execute(teamId, new PlayerId(10L), new PlayerId(11L)));

        assertEquals("Joueur introuvable dans l'équipe 1 : 10", exception.getMessage());

        verify(playerRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Doit lever une erreur si le remplaçant est introuvable dans l'équipe")
    void shouldThrowWhenReplacementPlayerIsMissingFromTeam() {
        TeamId teamId = new TeamId(1L);
        when(playerRepository.findByTeamId(teamId)).thenReturn(List.of(player(10L, teamId, true, "Starter")));

        PlayerNotFoundException exception = assertThrows(PlayerNotFoundException.class,
            () -> service.execute(teamId, new PlayerId(10L), new PlayerId(11L)));

        assertEquals("Joueur introuvable dans l'équipe 1 : 11", exception.getMessage());

        verify(playerRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Doit lever une erreur si un joueur existe mais pas dans l'équipe ciblée")
    void shouldThrowWhenPlayerExistsButNotInTargetTeam() {
        TeamId targetTeamId = new TeamId(1L);
        when(playerRepository.findByTeamId(targetTeamId)).thenReturn(List.of(player(10L, targetTeamId, true, "Starter")));

        PlayerNotFoundException exception = assertThrows(PlayerNotFoundException.class,
            () -> service.execute(targetTeamId, new PlayerId(10L), new PlayerId(11L)));

        assertEquals("Joueur introuvable dans l'équipe 1 : 11", exception.getMessage());

        verify(playerRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Doit refuser un échange si le joueur sortant n'est pas titulaire")
    void shouldRejectSwapWhenOutgoingPlayerIsNotStarter() {
        TeamId teamId = new TeamId(1L);
        when(playerRepository.findByTeamId(teamId)).thenReturn(List.of(
            player(10L, teamId, false, "Starter"),
            player(11L, teamId, false, "Bench")
        ));

        assertThrows(TitularisationNotAllowedException.class,
            () -> service.execute(teamId, new PlayerId(10L), new PlayerId(11L)));

        verify(playerRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Doit refuser un échange si le remplaçant est déjà titulaire")
    void shouldRejectSwapWhenReplacementIsAlreadyStarter() {
        TeamId teamId = new TeamId(1L);
        when(playerRepository.findByTeamId(teamId)).thenReturn(List.of(
            player(10L, teamId, true, "Starter"),
            player(11L, teamId, true, "Bench")
        ));

        assertThrows(TitularisationNotAllowedException.class,
            () -> service.execute(teamId, new PlayerId(10L), new PlayerId(11L)));

        verify(playerRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Doit refuser un échange avec le même joueur")
    void shouldRejectSwapWithSamePlayer() {
        TeamId teamId = new TeamId(1L);
        assertThrows(TitularisationNotAllowedException.class,
            () -> service.execute(teamId, new PlayerId(10L), new PlayerId(10L)));

        verify(playerRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    private Player player(Long playerId, TeamId teamId, boolean titulaire, String firstName) {
        Date now = new Date();
        return new Player(
            new PlayerIdentifier(new PlayerId(playerId), firstName, "Player", firstName.substring(0, 1), teamId),
            new PlayerStat(PositionEnum.CM, new Note(7.5f), new Price(BigDecimal.valueOf(5000.0)), titulaire),
            new PlayerVersion(0, now, now)
        );
    }
}

