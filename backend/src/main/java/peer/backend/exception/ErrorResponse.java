package peer.backend.exception;

import java.time.LocalDateTime;
import javax.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponse {

    public ErrorResponse(HttpServletRequest req, HttpStatus httpStatus, Exception exception) {
        this.timestamp = (LocalDateTime.now());
        this.statusCode = httpStatus.value();
        this.statusText = httpStatus.getReasonPhrase();
        this.message = exception.getMessage();
        this.path = req.getRequestURI();
    }

    private final LocalDateTime timestamp;
    private final int statusCode;
    private final String statusText;
    private final String message;
    private final String path;
}
