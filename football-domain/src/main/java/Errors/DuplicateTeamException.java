package Errors;

/**
 * Exception levée quand on essaie de créer une équipe avec un nom ou acronyme déjà existant
 */
public class DuplicateTeamException extends DomainException {
    public DuplicateTeamException(String message) {
        super(message);
    }
}

