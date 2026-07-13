package com.foot.adapter.rest.controller;

import Errors.PlayerNotFoundException;
import com.foot.adapter.rest.RestExceptionHandler;
import entity.Note;
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
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import usecase.*;

import java.math.BigDecimal;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
    classes = PlayerControllerIntegrationTest.TestConfig.class,
    properties = {
        "spring.flyway.enabled=false",
        "spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration"
    }
)
@AutoConfigureMockMvc
class PlayerControllerIntegrationTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import(RestExceptionHandler.class)
    static class TestConfig {
        @Bean
        GetPlayerDetailsUseCase getPlayerDetailsUseCase() {
            return Mockito.mock(GetPlayerDetailsUseCase.class);
        }

        @Bean
        RecruitPlayerUseCase recruitPlayerUseCase() {
            return Mockito.mock(RecruitPlayerUseCase.class);
        }

        @Bean
        TransferPlayerUseCase transferPlayerUseCase() {
            return Mockito.mock(TransferPlayerUseCase.class);
        }

        @Bean
        UpdatePlayerPerformanceUseCase updatePlayerPerformanceUseCase() {
            return Mockito.mock(UpdatePlayerPerformanceUseCase.class);
        }

        @Bean
        UpdatePlayerPriceUseCase updatePlayerPriceUseCase() {
            return Mockito.mock(UpdatePlayerPriceUseCase.class);
        }

        @Bean
        PlayerController playerController(
            GetPlayerDetailsUseCase getPlayerDetailsUseCase,
            RecruitPlayerUseCase recruitPlayerUseCase,
            TransferPlayerUseCase transferPlayerUseCase,
            UpdatePlayerPerformanceUseCase updatePlayerPerformanceUseCase,
            UpdatePlayerPriceUseCase updatePlayerPriceUseCase
        ) {
            return new PlayerController(getPlayerDetailsUseCase, recruitPlayerUseCase,
                transferPlayerUseCase,
                updatePlayerPerformanceUseCase, updatePlayerPriceUseCase);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GetPlayerDetailsUseCase getPlayerDetailsUseCase;

    @Autowired
    private RecruitPlayerUseCase recruitPlayerUseCase;

    @Autowired
    private TransferPlayerUseCase transferPlayerUseCase;

    @Autowired
    private UpdatePlayerPerformanceUseCase updatePlayerPerformanceUseCase;

    @Autowired
    private UpdatePlayerPriceUseCase updatePlayerPriceUseCase;

    @Test
    void shouldGetPlayer() throws Exception {
        Player p = new Player(
            new PlayerIdentifier(new PlayerId(5L), "Leo", "Messi", "LM", new TeamId(3L)),
            new PlayerStat(PositionEnum.RW, new Note(9.5f), new Price(BigDecimal.valueOf(1000.0)), true),
            new PlayerVersion(0, new Date(), new Date())
        );
        when(getPlayerDetailsUseCase.execute(new PlayerId(5L))).thenReturn(p);

        mockMvc.perform(get("/api/players/5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Leo"));
    }

    @Test
    void shouldRecruitTransferUpdate() throws Exception {
        when(recruitPlayerUseCase.execute(any(), any(), any(), any(), any(), any(), any())).thenReturn(55L);

        Transfer mockTransfer = new Transfer(
            new TransferId(100L),
            new PlayerId(55L),
            new TeamId(1L),
            new TeamId(2L),
            new Price(BigDecimal.valueOf(120)),
            new Date()
        );
        when(transferPlayerUseCase.execute(any(), any(), any(), any())).thenReturn(mockTransfer);
        doNothing().when(updatePlayerPerformanceUseCase).execute(any(), any());

        mockMvc.perform(post("/api/players/recruit")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"firstName":"A","lastName":"B","acronym":"AB","position":"st","performance":7.0,"marketPrice":100,"teamId":1}
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(55L));

        mockMvc.perform(post("/api/players/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"playerId":55,"sourceTeamId":1,"targetTeamId":2,"transferPrice":120}
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.transferId").value(100L))
            .andExpect(jsonPath("$.playerId").value(55L))
            .andExpect(jsonPath("$.sourceTeamId").value(1L))
            .andExpect(jsonPath("$.targetTeamId").value(2L));

        mockMvc.perform(patch("/api/players/55/performance")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"performance":8.2}
                    """))
            .andExpect(status().isNoContent());
    }

    @Test
    void shouldMapPlayerNotFound() throws Exception {
        when(getPlayerDetailsUseCase.execute(new PlayerId(404L))).thenThrow(new PlayerNotFoundException("missing"));

        mockMvc.perform(get("/api/players/404"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("missing"));
    }

    @Test
    void shouldMapOptimisticLockingFailureToConflict() throws Exception {
        doThrow(new ObjectOptimisticLockingFailureException(Player.class, 3L))
            .when(updatePlayerPriceUseCase).execute(any(), any());

        mockMvc.perform(patch("/api/players/3/price")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"price":100.0}
                    """))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.error").value("Conflict"));
    }

}
