package peer.backend.dto.privateinfo;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitTokenDTO {
    private String token;
    private Long code;
}
