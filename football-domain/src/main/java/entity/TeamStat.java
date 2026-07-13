package entity;

import Validator.NullValidator;

import java.math.BigDecimal;
import java.util.Date;

import static Errors.ErrorMessages.BUDGET_NE_PEUT_PAS_ÊTRE_NULL;

public record TeamStat(BigDecimal budget,
                       Date creation,
                       Date lastUpdate,
                       Integer version) {

    public TeamStat(BigDecimal budget, Date creation, Date lastUpdate) {
        this(budget, creation, lastUpdate, 0);
    }

    public TeamStat {
        NullValidator.requireNonNull(budget, BUDGET_NE_PEUT_PAS_ÊTRE_NULL);
    }

    public TeamStat incrementVersionWithBudget(BigDecimal updatedBudget) {
        Date now = new Date();
        int nextVersion = version == null ? 1 : version + 1;
        return new TeamStat(updatedBudget, creation, now, nextVersion);
    }
}
