package com.foot.adapter.rest.dto;

import entity.PositionEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record InitialPlayerRequest(
    @NotBlank String firstName,
    @NotBlank String lastName,
    @NotBlank String acronym,
    @NotNull PositionEnum position,
    @NotNull Float performance,
    @NotNull BigDecimal marketPrice
) {
}

