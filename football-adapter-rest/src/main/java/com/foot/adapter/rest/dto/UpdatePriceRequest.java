package com.foot.adapter.rest.dto;

import jakarta.validation.constraints.NotNull;

public record UpdatePriceRequest(@NotNull Float price) {
}
