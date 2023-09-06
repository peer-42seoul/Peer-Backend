package peer.backend.dto.security;

import lombok.Data;

@Data
public class EmailMessage {
    private String to;
    private String subject;
    private String text;
}
