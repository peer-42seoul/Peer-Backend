package peer.backend.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponse {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public ErrorResponse(HttpServletRequest req, HttpStatus httpStatus, Exception exception) {
        this.message = exception.getMessage();
    }

    private final String message;

    public String convertToJson() throws JsonProcessingException {
        return objectMapper.writeValueAsString(this);
    }
}
