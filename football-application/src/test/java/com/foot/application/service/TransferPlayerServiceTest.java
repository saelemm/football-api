package com.foot.application.service;

import Errors.PlayerNotFoundException;
import Errors.TeamNotFoundException;
import entity.Note;
import entity.Player;
import entity.PlayerId;
import entity.PlayerIdentifier;
import entity.PlayerStat;
import entity.PlayerVersion;
import entity.PositionEnum;
import entity.Price;
import entity.Team;
import entity.TeamId;
import entity.TeamIdentifier;
import entity.TeamStat;
import entity.Transfer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import port.IPlayerRepository;
import port.ITeamRepository;
import port.ITransferRepository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires de TransferPlayerService")
class TransferPlayerServiceTest {

    @Mock
    private IPlayerRepository playerRepository;

    @Mock
    private ITeamRepository teamRepository;

    @Mock
    private ITransferRepository transferRepository;

    @InjectMocks
    private TransferPlayerService service;

    @Test
    @DisplayName("Doit transferer un joueur avec mise a jour source et cible")
    void shouldTransferPlayerAndUpdateSourceAndTargetTeams() {
        PlayerId playerId = new PlayerId(3L);
        TeamId sourceTeamId = new TeamId(1L);
        TeamId targetTeamId = new TeamId(2L);

        Player player = player(3L, 1L);
        Team sourceTeam = team(1L, "PSG", "PSG", 10000.0, List.of(playerId));
        Team targetTeam = team(2L, "OM", "OM", 10000.0, List.of());

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));
        when(teamRepository.findById(targetTeamId)).thenReturn(Optional.of(targetTeam));
        when(teamRepository.findById(sourceTeamId)).thenReturn(Optional.of(sourceTeam));

        Transfer transfer = service.execute(playerId, sourceTeamId, targetTeamId, BigDecimal.valueOf(1200.0));

        assertEquals(playerId, transfer.playerId());
        assertEquals(sourceTeamId, transfer.sourceTeamId());
        assertEquals(targetTeamId, transfer.targetTeamId());

        verify(transferRepository).save(any(Transfer.class));
        verify(playerRepository).save(any(Player.class));
        verify(teamRepository, times(2)).save(any(Team.class));
    }

    @Test
    @DisplayName("Doit transferer sans equipe source")
    void shouldTransferWithoutSourceTeam() {
        PlayerId playerId = new PlayerId(3L);
        TeamId targetTeamId = new TeamId(2L);

        Player player = player(3L, 1L);
        Team targetTeam = team(2L, "OM", "OM", 10000.0, List.of());

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));
        when(teamRepository.findById(targetTeamId)).thenReturn(Optional.of(targetTeam));

        Transfer transfer = service.execute(playerId, null, targetTeamId, BigDecimal.valueOf(1200.0));

        assertNull(transfer.sourceTeamId());
        verify(teamRepository, times(1)).save(any(Team.class));
    }

    @Test
    @DisplayName("Doit lever une erreur si le joueur n'existe pas")
    void shouldThrowWhenPlayerNotFound() {
        PlayerId playerId = new PlayerId(404L);
        when(playerRepository.findById(playerId)).thenReturn(Optional.empty());

        assertThrows(PlayerNotFoundException.class,
            () -> service.execute(playerId, new TeamId(1L), new TeamId(2L), BigDecimal.TEN));

        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    @DisplayName("Doit lever une erreur si l'equipe cible n'existe pas")
    void shouldThrowWhenTargetTeamNotFound() {
        PlayerId playerId = new PlayerId(3L);
        TeamId targetTeamId = new TeamId(404L);

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player(3L, 1L)));
        when(teamRepository.findById(targetTeamId)).thenReturn(Optional.empty());

        assertThrows(TeamNotFoundException.class,
            () -> service.execute(playerId, new TeamId(1L), targetTeamId, BigDecimal.TEN));

        verify(transferRepository, never()).save(any(Transfer.class));
    }

    private Player player(Long playerId, Long teamId) {
        Date now = new Date();
        return new Player(
            new PlayerIdentifier(new PlayerId(playerId), "Kylian", "Mbappe", "KM", new TeamId(teamId)),
            new PlayerStat(PositionEnum.ST, new Note(8.5f), new Price(BigDecimal.valueOf(9000.0)), false),
            new PlayerVersion(0, now, now)
        );
    }

    private Team team(Long id, String name, String acronym, Double budget, List<PlayerId> playerIds) {
        Date now = new Date();
        return new Team(
            new TeamIdentifier(new TeamId(id), name, acronym),
            new TeamStat(BigDecimal.valueOf(budget), now, now),
            playerIds,
            List.of()
        );
    }
}

