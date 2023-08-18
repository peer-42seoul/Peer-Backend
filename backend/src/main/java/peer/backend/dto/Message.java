package peer.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private HttpStatus status;
    private String message;
    private Object dto;

    public Message(HttpStatus status, String message) {
        this(status, message, null);
    }
    public Message(HttpStatus status) {
        this(status, null, null);
    }
}
