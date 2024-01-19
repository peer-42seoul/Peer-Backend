package peer.backend.dto.profile;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class SkillDTO {
    private Long tagId;
    private String name;
    private String color;
}
