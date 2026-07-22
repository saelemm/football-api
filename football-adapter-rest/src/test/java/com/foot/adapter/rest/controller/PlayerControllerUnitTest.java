package com.foot.adapter.rest.controller;

import com.foot.adapter.rest.dto.RecruitPlayerRequest;
import com.foot.adapter.rest.dto.TransferPlayerRequest;
import com.foot.adapter.rest.dto.UpdatePerformanceRequest;
import com.foot.adapter.rest.dto.UpdatePriceRequest;
import entity.Player;
import entity.PlayerId;
import entity.PlayerIdentifier;
import entity.PlayerStat;
import entity.PlayerVersion;
import entity.PositionEnum;
import entity.Price;
import entity.TeamId;
import entity.Transfer;
import entity.TransferId;
import entity.Note;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import usecase.GetPlayerDetailsUseCase;
import usecase.RecruitPlayerUseCase;
import usecase.TransferPlayerUseCase;
import usecase.UpdatePlayerPerformanceUseCase;
import usecase.UpdatePlayerPriceUseCase;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlayerControllerUnitTest {

    @Mock
    private GetPlayerDetailsUseCase getPlayerDetailsUseCase;
    @Mock
    private RecruitPlayerUseCase recruitPlayerUseCase;
    @Mock
    private TransferPlayerUseCase transferPlayerUseCase;
    @Mock
    private UpdatePlayerPerformanceUseCase updatePlayerPerformanceUseCase;
    @Mock
    private UpdatePlayerPriceUseCase updatePlayerPriceUseCase;

    @InjectMocks
    private PlayerController controller;

    @Test
    void shouldGetPlayer() {
        Player player = new Player(
            new PlayerIdentifier(new PlayerId(1L), "Kylian", "Mbappe", "KM", new TeamId(2L)),
            new PlayerStat(PositionEnum.ST, new Note(8.5f), new Price(BigDecimal.valueOf(100.0)), true),
            new PlayerVersion(0, new Date(), new Date())
        );
        when(getPlayerDetailsUseCase.execute(any(PlayerId.class))).thenReturn(player);

        assertEquals("Kylian", controller.getPlayer(1L).firstName());
    }

    @Test
    void shouldRecruitTransferAndUpdate() {
        when(recruitPlayerUseCase.execute(any(), any(), any(), any(), any(), any(), any())).thenReturn(42L);

        Transfer mockTransfer = new Transfer(
            new TransferId(1L),
            new PlayerId(42L),
            new TeamId(1L),
            new TeamId(2L),
            new Price(BigDecimal.ONE),
            new Date()
        );
        when(transferPlayerUseCase.execute(any(), any(), any(), any())).thenReturn(mockTransfer);

        assertEquals(42L, controller.recruitPlayer(new RecruitPlayerRequest(
            "A", "B", "AB", PositionEnum.ST, 7.5f, BigDecimal.TEN, 1L
        )).id());

        var transferResponse = controller.transferPlayer(new TransferPlayerRequest(42L, 1L, 2L, BigDecimal.ONE));
        assertEquals(1L, transferResponse.transferId());
        assertEquals(42L, transferResponse.playerId());

        controller.updatePerformance(42L, new UpdatePerformanceRequest(9.1f));
        controller.updatePrice(42L, new UpdatePriceRequest(100.0f));

        verify(transferPlayerUseCase).execute(any(), any(), any(), any());
        verify(updatePlayerPerformanceUseCase).execute(any(), any());
        verify(updatePlayerPriceUseCase).execute(any(), any());
    }

}

