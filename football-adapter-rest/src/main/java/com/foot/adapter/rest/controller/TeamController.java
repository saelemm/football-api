package com.foot.adapter.rest.controller;

import com.foot.adapter.rest.dto.CreateTeamRequest;
import com.foot.adapter.rest.dto.IdResponse;
import com.foot.adapter.rest.dto.PlayerResponse;
import com.foot.adapter.rest.dto.TeamResponse;
import com.foot.adapter.rest.dto.TransferResponse;
import com.foot.adapter.rest.mapper.RestDtoMapper;
import entity.PlayerId;
import entity.TeamId;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usecase.CreateTeamUseCase;
import usecase.GetTeamDetailsUseCase;
import usecase.GetTeamTransferHistoryUseCase;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@Validated
public class TeamController {

    private final CreateTeamUseCase createTeamUseCase;
    private final GetTeamDetailsUseCase getTeamDetailsUseCase;
    private final GetTeamTransferHistoryUseCase getTeamTransferHistoryUseCase;

    public TeamController(
        CreateTeamUseCase createTeamUseCase,
        GetTeamDetailsUseCase getTeamDetailsUseCase,
        GetTeamTransferHistoryUseCase getTeamTransferHistoryUseCase
    ) {
        this.createTeamUseCase = createTeamUseCase;
        this.getTeamDetailsUseCase = getTeamDetailsUseCase;
        this.getTeamTransferHistoryUseCase = getTeamTransferHistoryUseCase;
    }

    @GetMapping
    public List<TeamResponse> getAllTeams() {
        return getTeamDetailsUseCase.findAllTeams().stream()
            .map(RestDtoMapper::toResponse)
            .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IdResponse createTeam(@RequestBody CreateTeamRequest request) {
        Long id = createTeamUseCase.execute(request.name(), request.acronym(), request.initialBudget());
        return new IdResponse(id);
    }

    @GetMapping("/{teamId}")
    public TeamResponse getTeam(@PathVariable Long teamId) {
        return RestDtoMapper.toResponse(getTeamDetailsUseCase.execute(new TeamId(teamId)));
    }

    @GetMapping("/{teamId}/players")
    public List<PlayerResponse> getCurrentPlayers(@PathVariable Long teamId) {
        return getTeamDetailsUseCase.findCurrentPlayers(new TeamId(teamId)).stream()
            .map(RestDtoMapper::toResponse)
            .toList();
    }

    @GetMapping("/{teamId}/players/starters")
    public List<PlayerResponse> getStarters(@PathVariable Long teamId) {
        return getTeamDetailsUseCase.findTitulaires(new TeamId(teamId)).stream()
            .map(RestDtoMapper::toResponse)
            .toList();
    }

    @GetMapping("/{teamId}/players/substitutes")
    public List<PlayerResponse> getSubstitutes(@PathVariable Long teamId) {
        return getTeamDetailsUseCase.findRemplacants(new TeamId(teamId)).stream()
            .map(RestDtoMapper::toResponse)
            .toList();
    }

    @GetMapping("/{teamId}/transfers")
    public List<TransferResponse> getAllTransfers(@PathVariable Long teamId) {
        return getTeamTransferHistoryUseCase.findAllTransfers(new TeamId(teamId)).stream()
            .map(RestDtoMapper::toResponse)
            .toList();
    }

    @GetMapping("/{teamId}/transfers/incoming")
    public List<TransferResponse> getIncomingTransfers(@PathVariable Long teamId) {
        return getTeamTransferHistoryUseCase.findIncoming(new TeamId(teamId)).stream()
            .map(RestDtoMapper::toResponse)
            .toList();
    }

    @GetMapping("/{teamId}/transfers/outgoing")
    public List<TransferResponse> getOutgoingTransfers(@PathVariable Long teamId) {
        return getTeamTransferHistoryUseCase.findOutgoing(new TeamId(teamId)).stream()
            .map(RestDtoMapper::toResponse)
            .toList();
    }

    @GetMapping("/{teamId}/transfers/player/{playerId}")
    public List<TransferResponse> getTransfersByPlayer(@PathVariable Long teamId, @PathVariable Long playerId) {
        return getTeamTransferHistoryUseCase.findByPlayerId(new TeamId(teamId), new PlayerId(playerId)).stream()
            .map(RestDtoMapper::toResponse)
            .toList();
    }
}

