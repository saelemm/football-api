package entity;

import Validator.NullValidator;
import Validator.NumberValidator;

import static Errors.ErrorMessages.NOTE_NULL;
import static Errors.ErrorMessages.NOTE_OUTBOUND;

/**
 * Classe encapsulant le principe de note d'un joueur sur une base float. La note pouvanat aller de 0 à 10.
 *
 * @param value de type float, la note attribué au joueur.
 */
public record Note(Float value) {
    public Note {
        NullValidator.requireNonNull(value, NOTE_NULL);
        NumberValidator.requireInRange(value, 0.0f, 10.0f, NOTE_OUTBOUND);
    }

    public Note improve(Float amount) {
        float newValue = Math.min(value + amount, 10.0f);
        return new Note(newValue);
    }

    public Note decline(Float amount) {
        float newValue = Math.max(value - amount, 0.0f);
        return new Note(newValue);
    }
}
