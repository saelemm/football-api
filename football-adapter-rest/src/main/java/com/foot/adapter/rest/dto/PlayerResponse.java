package com.foot.adapter.rest.dto;

import java.math.BigDecimal;
import java.util.Date;

public record PlayerResponse(
    Long id,
    String firstName,
    String lastName,
    String acronym,
    Long teamId,
    String position,
    Float performance,
    BigDecimal marketPrice,
    boolean titulaire,
    Integer version,
    Date createdAt,
    Date updatedAt
) {
}

