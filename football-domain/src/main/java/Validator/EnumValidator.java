package Validator;

import java.util.Arrays;

public final class EnumValidator {

    private EnumValidator() {}

    public static <E extends Enum<E>> E fromString(Class<E> enumClass, String value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }

        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> e.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Valeur inconnue '" + value + "' pour " + enumClass.getSimpleName()
                        )
                );
    }
}
