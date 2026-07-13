package entity;

import Errors.ErrorMessages;
import Validator.NullValidator;
import Validator.NumberValidator;

import java.math.BigDecimal;

/**
 * Entity représentant le prix de marché d'un joueur pouvant être échangé avec d'autres équipes via un transfert.
 * @param value
 */
public record Price(BigDecimal value) {
    public Price {
        NullValidator.requireNonNull(value, ErrorMessages.PRIX_NULL);
        NumberValidator.requirePositive(value, ErrorMessages.PRIX_ZERO);
    }

    public Price updateByAddition(BigDecimal addedValue) {
        BigDecimal result = value.add(addedValue);
        return new Price(result.max(BigDecimal.ZERO));
    }
}
