package entity;

/**
 * Identifiant d'un joueur.
 * Étend Id<Long> pour typer fortement les identifiants de joueurs
 */
public final class PlayerId extends Id<Long> {

    public PlayerId(Long value) {
        super(value);
    }
}

