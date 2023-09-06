package peer.backend.dto.security.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Data
public class ErrorDto {
    private Date timestamp;
    private Integer statusCode;
    private String statusText;
    private String message;
    private String path;

    public ErrorDto(String message, String path) {
        this.timestamp = new Date();
        this.statusCode = 401;
        this.statusText = "Unauthorized";
        this.message = message;
        this.path = path;
    }
}
