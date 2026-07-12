package Validator;

import java.math.BigDecimal;
import java.math.BigInteger;

import static Validator.NullValidator.requireNonNull;

public final class NumberValidator {

    private NumberValidator() {}

    public static void requirePositive(BigDecimal value, String message) {
        requireNonNull(value, message);
        if (value.doubleValue() < 0) {
            throw new IllegalArgumentException(message);
        }
    }

    public static <T extends Number> void requirePositive(T value, String message) {
        requireNonNull(value, message);
        if (value.doubleValue() < 0) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requireInRange(float value, float min, float max, String message) {
        if (min > 0.0f) {
            requirePositive(value, message);
        }

        requireNonNull(value, message);

        if (value < min || value > max) {
            throw new IllegalArgumentException(message);
        }
    }
}
