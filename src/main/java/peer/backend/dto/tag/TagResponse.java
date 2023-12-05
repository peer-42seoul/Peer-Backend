package peer.backend.dto.tag;

import java.time.LocalDate;
import lombok.Getter;
import peer.backend.entity.Tag;

@Getter
public class TagResponse {

    private final Long tagId;
    private final String name;
    private final String color;
    private final LocalDate createdAt;
    private final LocalDate updatedAt;

    public TagResponse(Tag tag) {
        this.tagId = tag.getId();
        this.name = tag.getName();
        this.color = tag.getColor();
        this.createdAt = tag.getCreatedAt().toLocalDate();
        this.updatedAt = tag.getUpdatedAt().toLocalDate();
    }
}