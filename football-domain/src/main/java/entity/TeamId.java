package entity;

/**
 * Value Object spécialisé pour l'identifiant d'une équipe
 * Étend Id<Long> pour typer fortement les identifiants d'équipes
 */
public final class TeamId extends Id<Long> {

    public TeamId(Long value) {
        super(value);
    }
}

