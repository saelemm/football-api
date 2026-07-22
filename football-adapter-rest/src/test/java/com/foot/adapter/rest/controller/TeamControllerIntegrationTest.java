package com.foot.adapter.rest.controller;

import Errors.PlayerNotFoundException;
import Errors.TeamNotFoundException;
import Errors.TitularisationNotAllowedException;
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
import pagination.PagedResult;
import usecase.CreateTeamUseCase;
import usecase.GetTeamDetailsUseCase;
import usecase.SwapPlayerTitularisationUseCase;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
        when(getTeamDetailsUseCase.findCurrentPlayers(new TeamId(10L), 0, 20, "name", "asc"))
            .thenReturn(new PagedResult<>(List.of(p), 0, 20, 1, 1, true, true, "name", "asc"));

        mockMvc.perform(get("/api/teams/10/players"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(1L));
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
    void shouldPromoteReplacementWithoutStarter() throws Exception {
        doNothing().when(swapPlayerTitularisationUseCase).execute(any(), any(), any());

        mockMvc.perform(patch("/api/teams/1/players/titularisation/swap")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"replacementPlayerId":56}
                    """))
            .andExpect(status().isNoContent());
    }

    @Test
    void shouldDemoteStarterWithoutReplacement() throws Exception {
        doNothing().when(swapPlayerTitularisationUseCase).execute(any(), any(), any());

        mockMvc.perform(patch("/api/teams/1/players/titularisation/swap")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"titulairePlayerId":55}
                    """))
            .andExpect(status().isNoContent());
    }

    @Test
    void shouldRejectSwapWithoutAnyPlayer() throws Exception {
        Mockito.doThrow(new TitularisationNotAllowedException("Au moins un joueur doit être renseigné"))
            .when(swapPlayerTitularisationUseCase)
            .execute(any(), any(), any());

        mockMvc.perform(patch("/api/teams/1/players/titularisation/swap")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Au moins un joueur doit être renseigné"));
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
        Player p = new Player(
            new PlayerIdentifier(new PlayerId(1L), "Kylian", "Mbappe", "KM", new TeamId(1L)),
            new PlayerStat(PositionEnum.ST, new Note(9.0f), new Price(BigDecimal.valueOf(150)), true),
            new PlayerVersion(0, new Date(), new Date())
        );
        when(getTeamDetailsUseCase.findAllTeams(0, 20, "name", "asc")).thenReturn(new PagedResult<>(List.of(
                new Team(new TeamIdentifier(new TeamId(1L), "Paris Saint Germains", "PSG"),
                        new TeamStat(BigDecimal.valueOf(1000.0), new Date(), new Date()),
                        List.of(), List.of())
        ), 0, 20, 1, 1, true, true, "name", "asc"));
        when(getTeamDetailsUseCase.findCurrentPlayersByTeamIds(List.of(new TeamId(1L))))
            .thenReturn(Map.of(1L, List.of(p)));

        mockMvc.perform(get("/api/teams"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(1L))
            .andExpect(jsonPath("$.content[0].players[0].lastName").value("Mbappe"))
            .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void shouldGetAllTeamsWithPaginationAndSorting() throws Exception {
        Player p = new Player(
            new PlayerIdentifier(new PlayerId(2L), "Pierre", "A", "PA", new TeamId(2L)),
            new PlayerStat(PositionEnum.CM, new Note(7.0f), new Price(BigDecimal.valueOf(20)), true),
            new PlayerVersion(0, new Date(), new Date())
        );
        when(getTeamDetailsUseCase.findAllTeams(1, 5, "budget", "desc")).thenReturn(new PagedResult<>(List.of(
            new Team(new TeamIdentifier(new TeamId(2L), "OM", "OM"),
                new TeamStat(BigDecimal.valueOf(900.0), new Date(), new Date()),
                List.of(), List.of())
        ), 1, 5, 11, 3, false, false, "budget", "desc"));
        when(getTeamDetailsUseCase.findCurrentPlayersByTeamIds(List.of(new TeamId(2L))))
            .thenReturn(Map.of(2L, List.of(p)));

        mockMvc.perform(get("/api/teams?page=1&size=5&sortBy=BUDGET&direction=DESC"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(2L))
            .andExpect(jsonPath("$.content[0].players[0].id").value(2L))
            .andExpect(jsonPath("$.page").value(1))
            .andExpect(jsonPath("$.size").value(5))
            .andExpect(jsonPath("$.totalElements").value(11))
            .andExpect(jsonPath("$.totalPages").value(3))
            .andExpect(jsonPath("$.sortBy").value("budget"))
            .andExpect(jsonPath("$.direction").value("desc"));
    }

    @Test
    void shouldGetCurrentPlayersWithPaginationAndSorting() throws Exception {
        Player p = new Player(
            new PlayerIdentifier(new PlayerId(9L), "A", "B", "AB", new TeamId(10L)),
            new PlayerStat(PositionEnum.CM, new Note(6.5f), new Price(BigDecimal.TEN), true),
            new PlayerVersion(0, new Date(), new Date())
        );
        when(getTeamDetailsUseCase.findCurrentPlayers(new TeamId(10L), 2, 3, "marketPrice", "desc"))
            .thenReturn(new PagedResult<>(List.of(p), 2, 3, 17, 6, false, false, "marketPrice", "desc"));

        mockMvc.perform(get("/api/teams/10/players?page=2&size=3&sortBy=MARKET_PRICE&direction=DESC"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(9L))
            .andExpect(jsonPath("$.page").value(2))
            .andExpect(jsonPath("$.totalElements").value(17));
    }

    @Test
    void shouldGetCurrentPlayersWithPerformanceSorting() throws Exception {
        Player p = new Player(
            new PlayerIdentifier(new PlayerId(8L), "Perf", "Player", "PP", new TeamId(10L)),
            new PlayerStat(PositionEnum.CM, new Note(9.1f), new Price(BigDecimal.TEN), true),
            new PlayerVersion(0, new Date(), new Date())
        );
        when(getTeamDetailsUseCase.findCurrentPlayers(new TeamId(10L), 0, 20, "performance", "desc"))
            .thenReturn(new PagedResult<>(List.of(p), 0, 20, 1, 1, true, true, "performance", "desc"));

        mockMvc.perform(get("/api/teams/10/players?sortBy=PERFORMANCE&direction=DESC"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(8L))
            .andExpect(jsonPath("$.sortBy").value("performance"))
            .andExpect(jsonPath("$.direction").value("desc"));
    }

    @Test
    void shouldGetStartersWithPaginationAndSorting() throws Exception {
        Player starter = new Player(
            new PlayerIdentifier(new PlayerId(5L), "Starter", "One", "SO", new TeamId(10L)),
            new PlayerStat(PositionEnum.ST, new Note(8.0f), new Price(BigDecimal.valueOf(99)), true),
            new PlayerVersion(0, new Date(), new Date())
        );
        when(getTeamDetailsUseCase.findTitulaires(new TeamId(10L), 1, 4, "acronym", "desc"))
            .thenReturn(new PagedResult<>(List.of(starter), 1, 4, 9, 3, false, false, "acronym", "desc"));

        mockMvc.perform(get("/api/teams/10/players/starters?page=1&size=4&sortBy=ACRONYM&direction=DESC"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(5L))
            .andExpect(jsonPath("$.page").value(1))
            .andExpect(jsonPath("$.size").value(4))
            .andExpect(jsonPath("$.sortBy").value("acronym"));
    }

    @Test
    void shouldGetSubstitutesWithPaginationAndSorting() throws Exception {
        Player substitute = new Player(
            new PlayerIdentifier(new PlayerId(6L), "Bench", "Two", "BT", new TeamId(10L)),
            new PlayerStat(PositionEnum.CM, new Note(6.0f), new Price(BigDecimal.valueOf(42)), false),
            new PlayerVersion(0, new Date(), new Date())
        );
        when(getTeamDetailsUseCase.findRemplacants(new TeamId(10L), 0, 2, "marketPrice", "desc"))
            .thenReturn(new PagedResult<>(List.of(substitute), 0, 2, 5, 3, true, false, "marketPrice", "desc"));

        mockMvc.perform(get("/api/teams/10/players/substitutes?page=0&size=2&sortBy=MARKET_PRICE&direction=DESC"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(6L))
            .andExpect(jsonPath("$.totalElements").value(5))
            .andExpect(jsonPath("$.direction").value("desc"));
    }
}
