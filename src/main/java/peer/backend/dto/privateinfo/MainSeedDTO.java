package peer.backend.dto.privateinfo;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MainSeedDTO {
    private String seed;
    private String code;
}
