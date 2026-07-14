package com.foot.adapter.rest.controller;

import com.foot.adapter.rest.dto.SortDirection;
import com.foot.adapter.rest.dto.TransferSortBy;
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
import pagination.PagedResult;
import usecase.GetTeamTransferHistoryUseCase;

import java.math.BigDecimal;
import java.util.Date;

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
        when(getTeamTransferHistoryUseCase.findAllTransfers(new TeamId(1L), 0, 20, "transferDate", "desc"))
            .thenReturn(new PagedResult<>(java.util.List.of(transfer), 0, 20, 1, 1, true, true, "transferDate", "desc"));

        var result = controller.getAllTransfers(1L, 0, 20, TransferSortBy.DATE, SortDirection.DESC);

        assertEquals(1, result.content().size());
        assertEquals(1L, result.content().getFirst().transferId());
        verify(getTeamTransferHistoryUseCase).findAllTransfers(new TeamId(1L), 0, 20, "transferDate", "desc");
    }

    @Test
    void shouldGetIncomingTransfers() {
        when(getTeamTransferHistoryUseCase.findIncoming(new TeamId(2L), 1, 5, "playerId", "asc"))
            .thenReturn(new PagedResult<>(java.util.List.of(), 1, 5, 0, 0, false, true, "playerId", "asc"));

        var result = controller.getIncomingTransfers(2L, 1, 5, TransferSortBy.PLAYER_ID, SortDirection.ASC);

        assertEquals(0, result.content().size());
        verify(getTeamTransferHistoryUseCase).findIncoming(new TeamId(2L), 1, 5, "playerId", "asc");
    }

    @Test
    void shouldGetOutgoingTransfers() {
        when(getTeamTransferHistoryUseCase.findOutgoing(new TeamId(3L), 2, 3, "transferPrice", "desc"))
            .thenReturn(new PagedResult<>(java.util.List.of(), 2, 3, 0, 0, false, true, "transferPrice", "desc"));

        var result = controller.getOutgoingTransfers(3L, 2, 3, TransferSortBy.TRANSFER_PRICE, SortDirection.DESC);

        assertEquals(0, result.content().size());
        verify(getTeamTransferHistoryUseCase).findOutgoing(new TeamId(3L), 2, 3, "transferPrice", "desc");
    }
}

