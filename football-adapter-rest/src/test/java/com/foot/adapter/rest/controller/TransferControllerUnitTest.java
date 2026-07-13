package com.foot.adapter.rest.controller;

import entity.PlayerId;
import entity.Price;
import entity.TeamId;
import entity.Transfer;
import entity.TransferId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import usecase.GetTeamTransferHistoryUseCase;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferControllerUnitTest {

    @Mock
    private GetTeamTransferHistoryUseCase getTeamTransferHistoryUseCase;

    @InjectMocks
    private TransferController controller;

    @Test
    void shouldGetAllTransfers() {
        Transfer transfer = new Transfer(
            new TransferId(1L),
            new PlayerId(10L),
            new TeamId(1L),
            new TeamId(2L),
            new Price(BigDecimal.valueOf(30_000_000)),
            new Date()
        );
        when(getTeamTransferHistoryUseCase.findAllTransfers(new TeamId(1L))).thenReturn(List.of(transfer));

        var result = controller.getAllTransfers(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().transferId());
        verify(getTeamTransferHistoryUseCase).findAllTransfers(new TeamId(1L));
    }

    @Test
    void shouldGetIncomingTransfers() {
        when(getTeamTransferHistoryUseCase.findIncoming(new TeamId(2L))).thenReturn(List.of());

        var result = controller.getIncomingTransfers(2L);

        assertEquals(0, result.size());
        verify(getTeamTransferHistoryUseCase).findIncoming(new TeamId(2L));
    }

    @Test
    void shouldGetOutgoingTransfers() {
        when(getTeamTransferHistoryUseCase.findOutgoing(new TeamId(3L))).thenReturn(List.of());

        var result = controller.getOutgoingTransfers(3L);

        assertEquals(0, result.size());
        verify(getTeamTransferHistoryUseCase).findOutgoing(new TeamId(3L));
    }
}

