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
        when(transferRepository.findByTeamId(teamId)).thenReturn(List.of(transfer));

        List<Transfer> result = service.findAllTransfers(teamId);

        assertEquals(1, result.size());
        assertEquals(transfer, result.get(0));
    }

    @Test
    @DisplayName("Doit retourner les transferts sortants")
    void shouldReturnOutgoingTransfers() {
        TeamId teamId = new TeamId(1L);
        Transfer transfer = transfer(2L, 11L, 1L, 3L, 4000.0);

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team(1L, "PSG", "PSG")));
        when(transferRepository.findOutgoingTransfers(teamId)).thenReturn(List.of(transfer));

        List<Transfer> result = service.findOutgoing(teamId);

        assertEquals(1, result.size());
        assertEquals(transfer, result.get(0));
    }

    @Test
    @DisplayName("Doit retourner les transferts entrants")
    void shouldReturnIncomingTransfers() {
        TeamId teamId = new TeamId(1L);
        Transfer transfer = transfer(3L, 12L, 4L, 1L, 3000.0);

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team(1L, "PSG", "PSG")));
        when(transferRepository.findIncomingTransfers(teamId)).thenReturn(List.of(transfer));

        List<Transfer> result = service.findIncoming(teamId);

        assertEquals(1, result.size());
        assertEquals(transfer, result.get(0));
    }

    @Test
    @DisplayName("Doit lever une erreur si l'equipe n'existe pas")
    void shouldThrowWhenTeamDoesNotExist() {
        TeamId teamId = new TeamId(404L);
        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        TeamNotFoundException exception = assertThrows(TeamNotFoundException.class,
            () -> service.findAllTransfers(teamId));

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

