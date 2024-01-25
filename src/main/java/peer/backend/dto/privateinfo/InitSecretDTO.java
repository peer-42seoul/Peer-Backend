package peer.backend.dto.privateinfo;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitSecretDTO {
    private String secret;
    private Long code;
}
