package peer.backend.dto.profile;

import lombok.*;
import peer.backend.entity.tag.Tag;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class SkillDTO {
    private Long tagId;
    private String name;
    private String color;

    public SkillDTO(Tag tag) {
        this.tagId = tag.getId();
        this.name = tag.getName();
        this.color = tag.getColor();
    }
}
