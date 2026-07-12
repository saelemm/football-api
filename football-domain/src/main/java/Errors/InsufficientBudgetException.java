package Errors;

public class InsufficientBudgetException extends DomainException {
    public InsufficientBudgetException(String message) {
        super(message);
    }
}
