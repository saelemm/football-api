package com.foot.adapter.rest.controller;

import Errors.PlayerNotFoundException;
import Errors.TeamNotFoundException;
import com.foot.adapter.rest.RestExceptionHandler;
import entity.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import usecase.CreateTeamUseCase;
import usecase.GetTeamDetailsUseCase;
import usecase.SwapPlayerTitularisationUseCase;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
    classes = TeamControllerIntegrationTest.TestConfig.class,
    properties = {
        "spring.flyway.enabled=false",
        "spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration"
    }
)
@AutoConfigureMockMvc
class TeamControllerIntegrationTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import(RestExceptionHandler.class)
    static class TestConfig {
        @Bean
        CreateTeamUseCase createTeamUseCase() {
            return Mockito.mock(CreateTeamUseCase.class);
        }

        @Bean
        GetTeamDetailsUseCase getTeamDetailsUseCase() {
            return Mockito.mock(GetTeamDetailsUseCase.class);
        }

        @Bean
        SwapPlayerTitularisationUseCase swapPlayerTitularisationUseCase() {
            return Mockito.mock(SwapPlayerTitularisationUseCase.class);
        }

        @Bean
        TeamController teamController(
            CreateTeamUseCase createTeamUseCase,
            GetTeamDetailsUseCase getTeamDetailsUseCase,
            SwapPlayerTitularisationUseCase swapPlayerTitularisationUseCase
        ) {
            return new TeamController(createTeamUseCase, getTeamDetailsUseCase, swapPlayerTitularisationUseCase);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CreateTeamUseCase createTeamUseCase;

    @Autowired
    private GetTeamDetailsUseCase getTeamDetailsUseCase;

    @Autowired
    private SwapPlayerTitularisationUseCase swapPlayerTitularisationUseCase;

    @Test
    void shouldCreateTeam() throws Exception {
        when(createTeamUseCase.execute(any(), any(), any())).thenReturn(7L);

        mockMvc.perform(post("/api/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"PSG","acronym":"PSG","initialBudget":2000}
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(7L));
    }

    @Test
    void shouldGetCurrentPlayers() throws Exception {
        Player p = new Player(
            new PlayerIdentifier(new PlayerId(1L), "A", "B", "AB", new TeamId(10L)),
            new PlayerStat(PositionEnum.CM, new Note(6.5f), new Price(BigDecimal.TEN), true),
            new PlayerVersion(0, new Date(), new Date())
        );
        when(getTeamDetailsUseCase.findCurrentPlayers(new TeamId(10L))).thenReturn(List.of(p));

        mockMvc.perform(get("/api/teams/10/players"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void shouldSwapTitularisation() throws Exception {
        doNothing().when(swapPlayerTitularisationUseCase).execute(any(), any(), any());

        mockMvc.perform(patch("/api/teams/1/players/titularisation/swap")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"titulairePlayerId":55,"replacementPlayerId":56}
                    """))
            .andExpect(status().isNoContent());
    }

    @Test
    void shouldMapDomainException() throws Exception {
        when(getTeamDetailsUseCase.execute(new TeamId(999L))).thenThrow(new TeamNotFoundException("not found"));

        mockMvc.perform(get("/api/teams/999"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("not found"));
    }

    @Test
    void shouldMapPlayerNotFoundForSwapInsideTeam() throws Exception {
        Mockito.doThrow(new PlayerNotFoundException("Joueur introuvable dans l'équipe 1 : 56"))
            .when(swapPlayerTitularisationUseCase)
            .execute(any(), any(), any());

        mockMvc.perform(patch("/api/teams/1/players/titularisation/swap")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"titulairePlayerId":55,"replacementPlayerId":56}
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Joueur introuvable dans l'équipe 1 : 56"));
    }

    @Test
    void shouldGetAllTeams() throws Exception {
        when(getTeamDetailsUseCase.findAllTeams()).thenReturn(List.of(
                new Team(new TeamIdentifier(new TeamId(1L), "Paris Saint Germains", "PSG"),
                        new TeamStat(BigDecimal.valueOf(1000.0), new Date(), new Date()),
                        List.of(), List.of())
        ));

        mockMvc.perform(get("/api/teams"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L));
    }
}
