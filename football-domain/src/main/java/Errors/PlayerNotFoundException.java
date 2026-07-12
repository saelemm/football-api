package Errors;

/**
 * Exception levée quand un joueur n'est pas trouvé
 */
public class PlayerNotFoundException extends DomainException {
    public PlayerNotFoundException(String message) {
        super(message);
    }
}

