package com.foot.adapter.rest.controller;

import com.foot.adapter.rest.dto.PageResponse;
import com.foot.adapter.rest.dto.SortDirection;
import com.foot.adapter.rest.dto.TransferSortBy;
import com.foot.adapter.rest.dto.TransferResponse;
import com.foot.adapter.rest.mapper.RestDtoMapper;
import entity.TeamId;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pagination.PagedResult;
import usecase.GetTeamTransferHistoryUseCase;

import java.util.function.Function;

@RestController
@RequestMapping("/api/teams/{teamId}/transfers")
@Validated
public class TransferController {

    private final GetTeamTransferHistoryUseCase getTeamTransferHistoryUseCase;

    public TransferController(GetTeamTransferHistoryUseCase getTeamTransferHistoryUseCase) {
        this.getTeamTransferHistoryUseCase = getTeamTransferHistoryUseCase;
    }

    @GetMapping
    public PageResponse<TransferResponse> getAllTransfers(
        @PathVariable Long teamId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "DATE") TransferSortBy sortBy,
        @RequestParam(defaultValue = "DESC") SortDirection direction
    ) {
        return toPageResponse(
            getTeamTransferHistoryUseCase.findAllTransfers(new TeamId(teamId), page, size, sortBy.value(), direction.value()),
            RestDtoMapper::toResponse
        );
    }

    @GetMapping("/incoming")
    public PageResponse<TransferResponse> getIncomingTransfers(
        @PathVariable Long teamId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "DATE") TransferSortBy sortBy,
        @RequestParam(defaultValue = "DESC") SortDirection direction
    ) {
        return toPageResponse(
            getTeamTransferHistoryUseCase.findIncoming(new TeamId(teamId), page, size, sortBy.value(), direction.value()),
            RestDtoMapper::toResponse
        );
    }

    @GetMapping("/outgoing")
    public PageResponse<TransferResponse> getOutgoingTransfers(
        @PathVariable Long teamId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "DATE") TransferSortBy sortBy,
        @RequestParam(defaultValue = "DESC") SortDirection direction
    ) {
        return toPageResponse(
            getTeamTransferHistoryUseCase.findOutgoing(new TeamId(teamId), page, size, sortBy.value(), direction.value()),
            RestDtoMapper::toResponse
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

