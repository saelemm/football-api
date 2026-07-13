package com.foot.application.service;

import Errors.TeamNotFoundException;
import entity.Player;
import entity.PlayerId;
import entity.PositionEnum;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires de RecruitPlayerService")
class RecruitPlayerServiceTest {

    @Mock
    private ITeamRepository teamRepository;

    @Mock
    private IPlayerRepository playerRepository;

    @Mock
    private ITransferRepository transferRepository;

    @InjectMocks
    private RecruitPlayerService service;

    @Test
    @DisplayName("Doit recruter un joueur, creer un transfert et mettre a jour l'equipe")
    void shouldRecruitPlayerCreateTransferAndUpdateTeam() {
        TeamId teamId = new TeamId(1L);
        Team team = team(1L, "PSG", "PSG", 20000.0);

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(playerRepository.save(any(Player.class))).thenReturn(77L);

        Long createdPlayerId = service.execute(
            "Kylian",
            "Mbappe",
            "KM",
            PositionEnum.ST,
            8.5f,
            BigDecimal.valueOf(9000.0),
            1L
        );

        assertEquals(77L, createdPlayerId);

        ArgumentCaptor<Player> playerCaptor = ArgumentCaptor.forClass(Player.class);
        verify(playerRepository).save(playerCaptor.capture());
        Player savedPlayer = playerCaptor.getValue();
        assertEquals("Kylian", savedPlayer.identifier().firstName());
        assertEquals(teamId, savedPlayer.identifier().teamId());

        ArgumentCaptor<Transfer> transferCaptor = ArgumentCaptor.forClass(Transfer.class);
        verify(transferRepository).save(transferCaptor.capture());
        Transfer savedTransfer = transferCaptor.getValue();
        assertEquals(77L, savedTransfer.playerId().value());
        assertEquals(1L, savedTransfer.targetTeamId().value());
        assertNull(savedTransfer.sourceTeamId());

        ArgumentCaptor<Team> updatedTeamCaptor = ArgumentCaptor.forClass(Team.class);
        verify(teamRepository).save(updatedTeamCaptor.capture());
        Team updatedTeam = updatedTeamCaptor.getValue();
        assertTrue(updatedTeam.playerIds().contains(new PlayerId(77L)));
        assertEquals(BigDecimal.valueOf(11000.0), updatedTeam.teamStat().budget());
    }

    @Test
    @DisplayName("Doit lever une erreur si l'equipe n'existe pas")
    void shouldThrowWhenTeamNotFound() {
        when(teamRepository.findById(new TeamId(404L))).thenReturn(Optional.empty());

        TeamNotFoundException exception = assertThrows(TeamNotFoundException.class,
            () -> service.execute("A", "B", "AB", PositionEnum.ST, 7.0f, BigDecimal.TEN, 404L));

        assertEquals("Équipe introuvable : 404", exception.getMessage());
    }

    private Team team(Long id, String name, String acronym, Double budget) {
        Date now = new Date();
        return new Team(
            new TeamIdentifier(new TeamId(id), name, acronym),
            new TeamStat(BigDecimal.valueOf(budget), now, now),
            List.of(),
            List.of()
        );
    }
}

