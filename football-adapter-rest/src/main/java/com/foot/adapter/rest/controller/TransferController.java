package com.foot.adapter.rest.controller;

import com.foot.adapter.rest.dto.TransferResponse;
import com.foot.adapter.rest.mapper.RestDtoMapper;
import entity.TeamId;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import usecase.GetTeamTransferHistoryUseCase;

import java.util.List;

@RestController
@RequestMapping("/api/teams/{teamId}/transfers")
@Validated
public class TransferController {

    private final GetTeamTransferHistoryUseCase getTeamTransferHistoryUseCase;

    public TransferController(GetTeamTransferHistoryUseCase getTeamTransferHistoryUseCase) {
        this.getTeamTransferHistoryUseCase = getTeamTransferHistoryUseCase;
    }

    @GetMapping
    public List<TransferResponse> getAllTransfers(@PathVariable Long teamId) {
        return getTeamTransferHistoryUseCase.findAllTransfers(new TeamId(teamId)).stream()
            .map(RestDtoMapper::toResponse)
            .toList();
    }

    @GetMapping("/incoming")
    public List<TransferResponse> getIncomingTransfers(@PathVariable Long teamId) {
        return getTeamTransferHistoryUseCase.findIncoming(new TeamId(teamId)).stream()
            .map(RestDtoMapper::toResponse)
            .toList();
    }

    @GetMapping("/outgoing")
    public List<TransferResponse> getOutgoingTransfers(@PathVariable Long teamId) {
        return getTeamTransferHistoryUseCase.findOutgoing(new TeamId(teamId)).stream()
            .map(RestDtoMapper::toResponse)
            .toList();
    }
}

