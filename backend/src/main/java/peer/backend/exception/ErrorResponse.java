package peer.backend.exception;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import java.time.LocalDateTime;
import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponse {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public ErrorResponse(HttpServletRequest req, HttpStatus httpStatus, Exception exception) {
//        this.timestamp = (LocalDateTime.now());
        this.statusCode = httpStatus.value();
        this.statusText = httpStatus.getReasonPhrase();
        this.message = exception.getMessage();
        this.path = req.getRequestURI();
    }

//    @JsonSerialize(using = LocalDateSerializer.class)
//    @JsonDeserialize(using = LocalDateDeserializer.class)
//    private final LocalDateTime timestamp;
    private final int statusCode;
    private final String statusText;
    private final String message;
    private final String path;

    public String convertToJson() throws JsonProcessingException {
        return objectMapper.writeValueAsString(this);
    }
}
