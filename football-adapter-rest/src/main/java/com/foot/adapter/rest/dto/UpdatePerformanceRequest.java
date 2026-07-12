package com.foot.adapter.rest.dto;

import jakarta.validation.constraints.NotNull;

public record UpdatePerformanceRequest(@NotNull Float performance) {
}

