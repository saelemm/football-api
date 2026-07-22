package com.foot.adapter.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateTeamRequest(
    @NotBlank String name,
    @NotBlank String acronym,
    @NotNull BigDecimal initialBudget,
    @NotNull @Valid InitialPlayerRequest initialPlayer
) {
}

