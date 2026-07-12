package Errors;

/**
 * Exception levée quand une équipe n'est pas trouvée
 */
public class TeamNotFoundException extends DomainException {
    public TeamNotFoundException(String message) {
        super(message);
    }
}

