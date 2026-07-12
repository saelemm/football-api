package com.foot.adapter.rest.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public record TeamResponse(
    Long id,
    String name,
    String acronym,
    BigDecimal budget,
    Date createdAt,
    Date updatedAt,
    List<Long> playerIds,
    List<TransferResponse> transferHistory
) {
}

