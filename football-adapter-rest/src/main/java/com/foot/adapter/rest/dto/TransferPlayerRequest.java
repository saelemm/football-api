package com.foot.adapter.rest.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferPlayerRequest(
    @NotNull Long playerId,
    Long sourceTeamId,
    @NotNull Long targetTeamId,
    @NotNull BigDecimal transferPrice
) {
}

