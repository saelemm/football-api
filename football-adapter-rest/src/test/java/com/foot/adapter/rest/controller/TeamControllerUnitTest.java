package com.foot.adapter.rest.controller;

import com.foot.adapter.rest.dto.CreateTeamRequest;
import com.foot.adapter.rest.dto.IdResponse;
import entity.Team;
import entity.TeamId;
import entity.TeamIdentifier;
import entity.TeamStat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import usecase.CreateTeamUseCase;
import usecase.GetTeamDetailsUseCase;
import usecase.GetTeamTransferHistoryUseCase;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamControllerUnitTest {

    @Mock
    private CreateTeamUseCase createTeamUseCase;

    @Mock
    private GetTeamDetailsUseCase getTeamDetailsUseCase;

    @Mock
    private GetTeamTransferHistoryUseCase getTeamTransferHistoryUseCase;

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
}

