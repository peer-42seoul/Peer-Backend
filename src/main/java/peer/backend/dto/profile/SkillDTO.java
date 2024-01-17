package peer.backend.dto.profile;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SkillDTO {
    private Long tagId;
    private String name;
    private String color;
}
