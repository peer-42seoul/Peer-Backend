package peer.backend.dto.message;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LetterTargetDTO {
    private long targetId;
    private String targetNickname;
    private String targetProfile;
}
