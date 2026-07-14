package com.foot.adapter.rest.dto;

import jakarta.validation.constraints.NotNull;

public record SwapPlayerTitularisationRequest(
    Long titulairePlayerId,
    Long replacementPlayerId
) {
}

