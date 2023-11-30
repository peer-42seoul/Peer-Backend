package peer.backend.exception;

public class AlreadyDeletedException extends RuntimeException{
    public AlreadyDeletedException(String msg) { super(msg); }
}
