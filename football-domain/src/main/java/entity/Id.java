package entity;

import Validator.NullValidator;

import static Errors.ErrorMessages.ID_NULL;

/**
 * Classe générique encapsulant le principe d'identifiant sous nimporte quel type.
 * Permet à TeamId & PlayerId de se différencier et d'éviter les collisions d'identifiant.
 * @param <T> Type générique
 */
public abstract class Id<T> {

    protected final T value;

    protected Id(T value) {
        this.value = NullValidator.requireNonNull(value, ID_NULL);
    }

    public T value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Id<?> other && value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
