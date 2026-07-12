package entity;

import Validator.NullValidator;

import java.math.BigDecimal;
import java.util.Date;

import static Errors.ErrorMessages.BUDGET_NE_PEUT_PAS_ÊTRE_NULL;

public record TeamStat(BigDecimal budget,
                       Date creation,
                       Date lastUpdate) {

    public TeamStat {
        NullValidator.requireNonNull(budget, BUDGET_NE_PEUT_PAS_ÊTRE_NULL);
    }
}
