package Errors;

public class TransferNotAllowedException extends DomainException {
    public TransferNotAllowedException(String message) {
        super(message);
    }
}
