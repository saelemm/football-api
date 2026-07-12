package entity;

/**
 * Value Object spécialisé pour l'identifiant d'un transfert
 * Étend Id<Long> pour typer fortement les identifiants de transferts
 */
public final class TransferId extends Id<Long> {

    public TransferId(Long value) {
        super(value);
    }
}

