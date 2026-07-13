package com.foot.adapter.rest.dto;

import jakarta.validation.constraints.NotNull;

public record SwapPlayerTitularisationRequest(
    @NotNull Long titulairePlayerId,
    @NotNull Long replacementPlayerId
) {
}

