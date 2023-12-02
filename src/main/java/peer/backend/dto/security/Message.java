package peer.backend.dto.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private HttpStatus status;
    private Object dto;

    public Message(HttpStatus status) {
        this.status = status;
        this.dto = null;
    }
}
