package com.foot.adapter.rest.controller;

import com.foot.adapter.rest.dto.CreateTeamRequest;
import com.foot.adapter.rest.dto.IdResponse;
import com.foot.adapter.rest.dto.PlayerSortBy;
import com.foot.adapter.rest.dto.SortDirection;
import com.foot.adapter.rest.dto.SwapPlayerTitularisationRequest;
import entity.Note;
import entity.Player;
import entity.PlayerId;
import entity.PlayerIdentifier;
import entity.PlayerStat;
import entity.PlayerVersion;
import entity.PositionEnum;
import entity.Price;
import entity.Team;
import entity.TeamId;
import entity.TeamIdentifier;
import entity.TeamStat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pagination.PagedResult;
import usecase.CreateTeamUseCase;
import usecase.GetTeamDetailsUseCase;
import usecase.SwapPlayerTitularisationUseCase;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamControllerUnitTest {

    @Mock
    private CreateTeamUseCase createTeamUseCase;

    @Mock
    private GetTeamDetailsUseCase getTeamDetailsUseCase;


    @Mock
    private SwapPlayerTitularisationUseCase swapPlayerTitularisationUseCase;

    @InjectMocks
    private TeamController controller;

    @Test
    void shouldCreateTeam() {
        when(createTeamUseCase.execute("PSG", "PSG", BigDecimal.valueOf(1000.0))).thenReturn(10L);

        IdResponse response = controller.createTeam(new CreateTeamRequest("PSG", "PSG", BigDecimal.valueOf(1000.0)));

        assertEquals(10L, response.id());
    }

    @Test
    void shouldGetTeam() {
        Team team = new Team(
            new TeamIdentifier(new TeamId(1L), "PSG", "PSG"),
            new TeamStat(BigDecimal.valueOf(1200.0), new Date(), new Date()),
            List.of(),
            List.of()
        );
        when(getTeamDetailsUseCase.execute(new TeamId(1L))).thenReturn(team);

        assertEquals("PSG", controller.getTeam(1L).name());
    }

    @Test
    void shouldSwapTitularisation() {
        controller.swapTitularisation(7L, new SwapPlayerTitularisationRequest(10L, 11L));

        verify(swapPlayerTitularisationUseCase).execute(new TeamId(7L), new PlayerId(10L), new PlayerId(11L));
    }

    @Test
    void shouldPromoteReplacementWithoutStarter() {
        controller.swapTitularisation(7L, new SwapPlayerTitularisationRequest(null, 11L));

        verify(swapPlayerTitularisationUseCase).execute(new TeamId(7L), null, new PlayerId(11L));
    }

    @Test
    void shouldDemoteStarterWithoutReplacement() {
        controller.swapTitularisation(7L, new SwapPlayerTitularisationRequest(10L, null));

        verify(swapPlayerTitularisationUseCase).execute(new TeamId(7L), new PlayerId(10L), null);
    }

    @Test
    void shouldGetStarters() {
        Player player = player(10L, true);
        when(getTeamDetailsUseCase.findTitulaires(new TeamId(1L), 0, 20, "name", "asc"))
            .thenReturn(new PagedResult<>(List.of(player), 0, 20, 1, 1, true, true, "name", "asc"));

        var response = controller.getStarters(1L, 0, 20, PlayerSortBy.NAME, SortDirection.ASC);

        assertEquals(1, response.content().size());
        assertEquals(10L, response.content().getFirst().id());
    }

    @Test
    void shouldGetPlayersWithPerformanceSorting() {
        Player player = player(12L, true);
        when(getTeamDetailsUseCase.findCurrentPlayers(new TeamId(1L), 0, 20, "performance", "desc"))
            .thenReturn(new PagedResult<>(List.of(player), 0, 20, 1, 1, true, true, "performance", "desc"));

        var response = controller.getCurrentPlayers(1L, 0, 20, PlayerSortBy.PERFORMANCE, SortDirection.DESC);

        assertEquals(1, response.content().size());
        assertEquals(12L, response.content().getFirst().id());
        assertEquals("performance", response.sortBy());
        assertEquals("desc", response.direction());
    }

    @Test
    void shouldGetSubstitutes() {
        Player player = player(11L, false);
        when(getTeamDetailsUseCase.findRemplacants(new TeamId(1L), 1, 5, "marketPrice", "desc"))
            .thenReturn(new PagedResult<>(List.of(player), 1, 5, 6, 2, false, true, "marketPrice", "desc"));

        var response = controller.getSubstitutes(1L, 1, 5, PlayerSortBy.MARKET_PRICE, SortDirection.DESC);

        assertEquals(1, response.content().size());
        assertEquals(11L, response.content().getFirst().id());
        assertEquals("marketPrice", response.sortBy());
    }

    private Player player(Long id, boolean titulaire) {
        Date now = new Date();
        return new Player(
            new PlayerIdentifier(new PlayerId(id), "Player", "Test", "PT", new TeamId(1L)),
            new PlayerStat(PositionEnum.CM, new Note(7.0f), new Price(BigDecimal.TEN), titulaire),
            new PlayerVersion(0, now, now)
        );
    }
}

