package peer.backend.dto.tag;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDate;
import lombok.Getter;
import peer.backend.entity.tag.Tag;

@Getter
public class TagResponse {

    private final Long tagId;
    private final String name;
    private final String color;
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private final LocalDate createdAt;
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private final LocalDate updatedAt;

    public TagResponse(Tag tag) {
        this.tagId = tag.getId();
        this.name = tag.getName();
        this.color = tag.getColor();
        this.createdAt = tag.getCreatedAt().toLocalDate();
        this.updatedAt = tag.getUpdatedAt().toLocalDate();
    }
}