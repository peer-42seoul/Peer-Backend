package peer.backend.entity.board.recruit;

import org.springframework.stereotype.Component;
import peer.backend.dto.board.recruit.TagListResponse;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class TagListManager {
    private final List<Tag> predefinedTags = new ArrayList<>();

    @PostConstruct
    public void init() {
        predefinedTags.add(new Tag("Java", "#9AFE2E"));
        predefinedTags.add(new Tag("JavaScript", "#045FB4"));
        predefinedTags.add(new Tag("React", "#FF8000"));
        predefinedTags.add(new Tag("SpringBoot", "#FE2EC8"));
    }

    public List<Tag> getPredefinedTags() {
        return predefinedTags;
    }

    public List<TagListResponse> getRecruitTagList(List<String> recruitTags) {
        List<TagListResponse> result = new ArrayList<>();

        for (String recruitTag : recruitTags) {
            for (Tag tag : predefinedTags) {
                if (recruitTag.equals(tag.getName())) {
                    result.add(new TagListResponse(tag.getName(), tag.getColor()));
                }
            }
        }
        return result;
    }
}
