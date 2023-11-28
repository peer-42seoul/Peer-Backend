package peer.backend.dto.tag;

import lombok.Getter;
import peer.backend.entity.Tag;

@Getter
public class TagResponse {

    private String tag;
    private String color;

    public TagResponse(Tag tag) {
        this.tag = tag.getTag();
        this.color = tag.getColor();
    }
}
