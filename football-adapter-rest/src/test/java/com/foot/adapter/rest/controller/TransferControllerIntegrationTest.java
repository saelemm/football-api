package com.foot.adapter.rest.controller;

import Errors.TeamNotFoundException;
import com.foot.adapter.rest.RestExceptionHandler;
import entity.PlayerId;
import entity.Price;
import entity.TeamId;
import entity.Transfer;
import entity.TransferId;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import pagination.PagedResult;
import usecase.GetTeamTransferHistoryUseCase;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
    classes = TransferControllerIntegrationTest.TestConfig.class,
    properties = {
        "spring.flyway.enabled=false",
        "spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration"
    }
)
@AutoConfigureMockMvc
class TransferControllerIntegrationTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import(RestExceptionHandler.class)
    static class TestConfig {
        @Bean
        GetTeamTransferHistoryUseCase getTeamTransferHistoryUseCase() {
            return Mockito.mock(GetTeamTransferHistoryUseCase.class);
        }

        @Bean
        TransferController transferController(GetTeamTransferHistoryUseCase getTeamTransferHistoryUseCase) {
            return new TransferController(getTeamTransferHistoryUseCase);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GetTeamTransferHistoryUseCase getTeamTransferHistoryUseCase;

    @Test
    void shouldGetAllTransfers() throws Exception {
        Transfer transfer = new Transfer(
            new TransferId(9L),
            new PlayerId(7L),
            new TeamId(1L),
            new TeamId(2L),
            new Price(BigDecimal.valueOf(25_000_000)),
            new Date()
        );
        when(getTeamTransferHistoryUseCase.findAllTransfers(new TeamId(1L), 0, 20, "transferDate", "desc"))
            .thenReturn(new PagedResult<>(List.of(transfer), 0, 20, 1, 1, true, true, "transferDate", "desc"));

        mockMvc.perform(get("/api/teams/1/transfers"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].transferId").value(9L))
            .andExpect(jsonPath("$.content[0].playerId").value(7L))
            .andExpect(jsonPath("$.sortBy").value("transferDate"));
    }

    @Test
    void shouldGetIncomingTransfers() throws Exception {
        when(getTeamTransferHistoryUseCase.findIncoming(new TeamId(2L), 1, 5, "playerId", "asc"))
            .thenReturn(new PagedResult<>(List.of(), 1, 5, 0, 0, false, true, "playerId", "asc"));

        mockMvc.perform(get("/api/teams/2/transfers/incoming?page=1&size=5&sortBy=PLAYER_ID&direction=ASC"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.page").value(1))
            .andExpect(jsonPath("$.sortBy").value("playerId"));
    }

    @Test
    void shouldGetOutgoingTransfers() throws Exception {
        when(getTeamTransferHistoryUseCase.findOutgoing(new TeamId(3L), 2, 3, "transferPrice", "desc"))
            .thenReturn(new PagedResult<>(List.of(), 2, 3, 0, 0, false, true, "transferPrice", "desc"));

        mockMvc.perform(get("/api/teams/3/transfers/outgoing?page=2&size=3&sortBy=TRANSFER_PRICE&direction=DESC"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.page").value(2))
            .andExpect(jsonPath("$.direction").value("desc"));
    }

    @Test
    void shouldMapTeamNotFound() throws Exception {
        when(getTeamTransferHistoryUseCase.findAllTransfers(new TeamId(999L), 0, 20, "transferDate", "desc"))
            .thenThrow(new TeamNotFoundException("missing team"));

        mockMvc.perform(get("/api/teams/999/transfers"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("missing team"));
    }
}

