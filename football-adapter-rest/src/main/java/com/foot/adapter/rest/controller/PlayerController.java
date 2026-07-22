package com.foot.adapter.rest.controller;

import com.foot.adapter.rest.dto.*;
import com.foot.adapter.rest.mapper.RestDtoMapper;
import entity.PlayerId;
import entity.TeamId;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usecase.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/players")
@Validated
public class PlayerController {

    private final GetPlayerDetailsUseCase getPlayerDetailsUseCase;
    private final RecruitPlayerUseCase recruitPlayerUseCase;
    private final TransferPlayerUseCase transferPlayerUseCase;
    private final UpdatePlayerPerformanceUseCase updatePlayerPerformanceUseCase;
    private final UpdatePlayerPriceUseCase updatePlayerPriceUseCase;

    public PlayerController(
        GetPlayerDetailsUseCase getPlayerDetailsUseCase,
        RecruitPlayerUseCase recruitPlayerUseCase,
        TransferPlayerUseCase transferPlayerUseCase,
        UpdatePlayerPerformanceUseCase updatePlayerPerformanceUseCase,
        UpdatePlayerPriceUseCase updatePlayerPriceUseCase
    ) {
        this.getPlayerDetailsUseCase = getPlayerDetailsUseCase;
        this.recruitPlayerUseCase = recruitPlayerUseCase;
        this.transferPlayerUseCase = transferPlayerUseCase;
        this.updatePlayerPerformanceUseCase = updatePlayerPerformanceUseCase;
        this.updatePlayerPriceUseCase = updatePlayerPriceUseCase;
    }

    @GetMapping("/{playerId}")
    public PlayerResponse getPlayer(@PathVariable Long playerId) {
        return RestDtoMapper.toResponse(getPlayerDetailsUseCase.execute(new PlayerId(playerId)));
    }

    @PostMapping("/recruit")
    @ResponseStatus(HttpStatus.CREATED)
    public IdResponse recruitPlayer(@RequestBody RecruitPlayerRequest request) {
        Long id = recruitPlayerUseCase.execute(
            request.firstName(),
            request.lastName(),
            request.acronym(),
            request.position(),
            request.performance(),
            request.marketPrice(),
            request.teamId()
        );
        return new IdResponse(id);
    }

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.CREATED)
    public TransferResponse transferPlayer(@RequestBody TransferPlayerRequest request) {
        var transfer = transferPlayerUseCase.execute(
            new PlayerId(request.playerId()),
            request.sourceTeamId() == null ? null : new TeamId(request.sourceTeamId()),
            new TeamId(request.targetTeamId()),
            request.transferPrice()
        );
        return RestDtoMapper.toResponse(transfer);
    }


    @PatchMapping("/{playerId}/performance")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePerformance(@PathVariable Long playerId, @RequestBody UpdatePerformanceRequest request) {
        updatePlayerPerformanceUseCase.execute(new PlayerId(playerId), request.performance());
    }

    @PatchMapping("/{playerId}/price")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePrice(@PathVariable Long playerId, @RequestBody UpdatePriceRequest request) {
        updatePlayerPriceUseCase.execute(new PlayerId(playerId), BigDecimal.valueOf(request.price()));
    }
}

