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
        when(getTeamTransferHistoryUseCase.findAllTransfers(new TeamId(1L))).thenReturn(List.of(transfer));

        mockMvc.perform(get("/api/teams/1/transfers"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].transferId").value(9L))
            .andExpect(jsonPath("$[0].playerId").value(7L));
    }

    @Test
    void shouldGetIncomingTransfers() throws Exception {
        when(getTeamTransferHistoryUseCase.findIncoming(new TeamId(2L))).thenReturn(List.of());

        mockMvc.perform(get("/api/teams/2/transfers/incoming"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldGetOutgoingTransfers() throws Exception {
        when(getTeamTransferHistoryUseCase.findOutgoing(new TeamId(3L))).thenReturn(List.of());

        mockMvc.perform(get("/api/teams/3/transfers/outgoing"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldMapTeamNotFound() throws Exception {
        when(getTeamTransferHistoryUseCase.findAllTransfers(new TeamId(999L)))
            .thenThrow(new TeamNotFoundException("missing team"));

        mockMvc.perform(get("/api/teams/999/transfers"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("missing team"));
    }
}

