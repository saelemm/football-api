package com.foot.adapter.rest.controller;

import com.foot.adapter.rest.dto.CreateTeamRequest;
import com.foot.adapter.rest.dto.IdResponse;
import com.foot.adapter.rest.dto.PageResponse;
import com.foot.adapter.rest.dto.PlayerSortBy;
import com.foot.adapter.rest.dto.PlayerResponse;
import com.foot.adapter.rest.dto.SortDirection;
import com.foot.adapter.rest.dto.SwapPlayerTitularisationRequest;
import com.foot.adapter.rest.dto.TeamSortBy;
import com.foot.adapter.rest.dto.TeamResponse;
import com.foot.adapter.rest.mapper.RestDtoMapper;
import entity.PlayerId;
import entity.TeamId;
import pagination.PagedResult;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usecase.CreateTeamUseCase;
import usecase.GetTeamDetailsUseCase;
import usecase.SwapPlayerTitularisationUseCase;

import java.awt.print.Pageable;
import java.util.function.Function;

@RestController
@RequestMapping("/api/teams")
@Validated
public class TeamController {

    private final CreateTeamUseCase createTeamUseCase;
    private final GetTeamDetailsUseCase getTeamDetailsUseCase;
    private final SwapPlayerTitularisationUseCase swapPlayerTitularisationUseCase;

    public TeamController(
        CreateTeamUseCase createTeamUseCase,
        GetTeamDetailsUseCase getTeamDetailsUseCase,
        SwapPlayerTitularisationUseCase swapPlayerTitularisationUseCase
    ) {
        this.createTeamUseCase = createTeamUseCase;
        this.getTeamDetailsUseCase = getTeamDetailsUseCase;
        this.swapPlayerTitularisationUseCase = swapPlayerTitularisationUseCase;
    }

    @GetMapping
    public PageResponse<TeamResponse> getAllTeams(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "NAME") TeamSortBy sortBy,
        @RequestParam(defaultValue = "ASC") SortDirection direction
    ) {
        return toPageResponse(
            getTeamDetailsUseCase.findAllTeams(page, size, sortBy.value(), direction.value()),
            RestDtoMapper::toResponse
        );
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
    public PageResponse<PlayerResponse> getCurrentPlayers(
        @PathVariable Long teamId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "NAME") PlayerSortBy sortBy,
        @RequestParam(defaultValue = "ASC") SortDirection direction
    ) {
        return toPageResponse(
            getTeamDetailsUseCase.findCurrentPlayers(new TeamId(teamId), page, size, sortBy.value(), direction.value()),
            RestDtoMapper::toResponse
        );
    }

    @GetMapping("/{teamId}/players/starters")
    public PageResponse<PlayerResponse> getStarters(
        @PathVariable Long teamId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "NAME") PlayerSortBy sortBy,
        @RequestParam(defaultValue = "ASC") SortDirection direction
    ) {
        return toPageResponse(
            getTeamDetailsUseCase.findTitulaires(new TeamId(teamId), page, size, sortBy.value(), direction.value()),
            RestDtoMapper::toResponse
        );
    }

    @GetMapping("/{teamId}/players/substitutes")
    public PageResponse<PlayerResponse> getSubstitutes(
        @PathVariable Long teamId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "NAME") PlayerSortBy sortBy,
        @RequestParam(defaultValue = "ASC") SortDirection direction
    ) {
        return toPageResponse(
            getTeamDetailsUseCase.findRemplacants(new TeamId(teamId), page, size, sortBy.value(), direction.value()),
            RestDtoMapper::toResponse
        );
    }

    @PatchMapping("/{teamId}/players/titularisation/swap")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void swapTitularisation(@PathVariable Long teamId, @RequestBody SwapPlayerTitularisationRequest request) {
        swapPlayerTitularisationUseCase.execute(
            new TeamId(teamId),
            request.titulairePlayerId() == null ? null : new PlayerId(request.titulairePlayerId()),
            request.replacementPlayerId() == null ? null : new PlayerId(request.replacementPlayerId())
        );
    }

    private <I, O> PageResponse<O> toPageResponse(PagedResult<I> page, Function<I, O> mapper) {
        return new PageResponse<>(
            page.content().stream().map(mapper).toList(),
            page.page(),
            page.size(),
            page.totalElements(),
            page.totalPages(),
            page.first(),
            page.last(),
            page.sortBy(),
            page.direction()
        );
    }

}

