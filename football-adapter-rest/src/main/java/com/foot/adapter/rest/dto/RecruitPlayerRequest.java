package com.foot.adapter.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RecruitPlayerRequest(
    @NotBlank String firstName,
    @NotBlank String lastName,
    @NotBlank String acronym,
    @NotBlank String position,
    @NotNull Float performance,
    @NotNull BigDecimal marketPrice,
    @NotNull Long teamId
) {
}

