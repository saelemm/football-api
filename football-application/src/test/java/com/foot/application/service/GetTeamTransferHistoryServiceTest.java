package com.foot.application.service;

import Errors.TeamNotFoundException;
import entity.PlayerId;
import entity.Price;
import entity.Team;
import entity.TeamId;
import entity.TeamIdentifier;
import entity.TeamStat;
import entity.Transfer;
import entity.TransferId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pagination.PagedResult;
import port.ITeamRepository;
import port.ITransferRepository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires de GetTeamTransferHistoryService")
class GetTeamTransferHistoryServiceTest {

    @Mock
    private ITransferRepository transferRepository;

    @Mock
    private ITeamRepository teamRepository;

    @InjectMocks
    private GetTeamTransferHistoryService service;

    @Test
    @DisplayName("Doit retourner tous les transferts de l'equipe")
    void shouldReturnAllTransfersForTeam() {
        TeamId teamId = new TeamId(1L);
        Transfer transfer = transfer(1L, 10L, 2L, 1L, 5000.0);

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team(1L, "PSG", "PSG")));
        when(transferRepository.findByTeamId(teamId, 0, 20, "transferDate", "desc"))
            .thenReturn(new PagedResult<>(java.util.List.of(transfer), 0, 20, 1, 1, true, true, "transferDate", "desc"));

        PagedResult<Transfer> result = service.findAllTransfers(teamId, 0, 20, "transferDate", "desc");

        assertEquals(1, result.content().size());
        assertEquals(transfer, result.content().get(0));
    }

    @Test
    @DisplayName("Doit retourner les transferts sortants")
    void shouldReturnOutgoingTransfers() {
        TeamId teamId = new TeamId(1L);
        Transfer transfer = transfer(2L, 11L, 1L, 3L, 4000.0);

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team(1L, "PSG", "PSG")));
        when(transferRepository.findOutgoingTransfers(teamId, 1, 5, "transferPrice", "asc"))
            .thenReturn(new PagedResult<>(java.util.List.of(transfer), 1, 5, 6, 2, false, true, "transferPrice", "asc"));

        PagedResult<Transfer> result = service.findOutgoing(teamId, 1, 5, "transferPrice", "asc");

        assertEquals(1, result.content().size());
        assertEquals(transfer, result.content().get(0));
    }

    @Test
    @DisplayName("Doit retourner les transferts entrants")
    void shouldReturnIncomingTransfers() {
        TeamId teamId = new TeamId(1L);
        Transfer transfer = transfer(3L, 12L, 4L, 1L, 3000.0);

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team(1L, "PSG", "PSG")));
        when(transferRepository.findIncomingTransfers(teamId, 2, 3, "playerId", "desc"))
            .thenReturn(new PagedResult<>(java.util.List.of(transfer), 2, 3, 7, 3, false, true, "playerId", "desc"));

        PagedResult<Transfer> result = service.findIncoming(teamId, 2, 3, "playerId", "desc");

        assertEquals(1, result.content().size());
        assertEquals(transfer, result.content().get(0));
    }

    @Test
    @DisplayName("Doit lever une erreur si l'equipe n'existe pas")
    void shouldThrowWhenTeamDoesNotExist() {
        TeamId teamId = new TeamId(404L);
        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        TeamNotFoundException exception = assertThrows(TeamNotFoundException.class,
            () -> service.findAllTransfers(teamId, 0, 20, "transferDate", "desc"));

        assertEquals("Équipe introuvable : 404", exception.getMessage());
    }

    private Team team(Long id, String name, String acronym) {
        Date now = new Date();
        return new Team(
            new TeamIdentifier(new TeamId(id), name, acronym),
            new TeamStat(BigDecimal.valueOf(20000.0), now, now),
            List.of(),
            List.of()
        );
    }

    private Transfer transfer(Long transferId, Long playerId, Long sourceTeamId, Long targetTeamId, Double price) {
        return new Transfer(
            new TransferId(transferId),
            new PlayerId(playerId),
            sourceTeamId == null ? null : new TeamId(sourceTeamId),
            new TeamId(targetTeamId),
            new Price(BigDecimal.valueOf(price)),
            new Date()
        );
    }
}

