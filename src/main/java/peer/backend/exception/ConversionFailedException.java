package peer.backend.exception;

public class ConversionFailedException extends RuntimeException {
    public ConversionFailedException(String message) {
        super(message);
    }
}
