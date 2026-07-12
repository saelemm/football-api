package com.foot.adapter.rest.dto;

import java.math.BigDecimal;
import java.util.Date;

public record TransferResponse(
    Long transferId,
    Long playerId,
    Long sourceTeamId,
    Long targetTeamId,
    BigDecimal transferPrice,
    Date transferDate
) {
}

