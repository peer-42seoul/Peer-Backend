package peer.backend.exception;

public class ConstraintViolationException extends RuntimeException{
    public ConstraintViolationException(String message){ super(message); }
}
