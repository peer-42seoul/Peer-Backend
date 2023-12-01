package peer.backend.dto.tag;

import lombok.Getter;
import peer.backend.entity.Tag;

@Getter
public class TagResponse {

    private String name;
    private String color;

    public TagResponse(Tag tag) {
        this.name = tag.getName();
        this.color = tag.getColor();
    }
}
