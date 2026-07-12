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

    public Price increaseBy(BigDecimal addedValue) {
        return new Price(value.add(addedValue));
    }

    public Price increaseByPercentage(BigDecimal percentage) {
        NumberValidator.requirePositive(percentage, ErrorMessages.POURCENTAGE_NEGATIF);
        BigDecimal increase = value.multiply(percentage).divide(BigDecimal.valueOf(100));
        return new Price(value.add(increase));
    }
}
