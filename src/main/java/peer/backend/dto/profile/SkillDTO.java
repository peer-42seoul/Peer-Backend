package peer.backend.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class SkillDTO {
    private Long tagId;
    private String name;
    private String color;
}
