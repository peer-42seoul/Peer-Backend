package peer.backend.entity.board.recruit;

import lombok.Getter;
import org.springframework.stereotype.Component;
import peer.backend.dto.board.recruit.TagListResponse;

import java.util.ArrayList;
import java.util.List;

@Component
public class TagListManager {
    @Getter
    private static final List<Tag> predefinedTags = new ArrayList<>();

    static {
        predefinedTags.add(new Tag("Java", "#9AFE2E"));
        predefinedTags.add(new Tag("JavaScript", "#045FB4"));
        predefinedTags.add(new Tag("React", "#FF8000"));
        predefinedTags.add(new Tag("SpringBoot", "#FE2EC8"));
    }

}
