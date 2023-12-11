package peer.backend.service;

import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import peer.backend.dto.tag.TagResponse;
import peer.backend.entity.tag.RecruitTag;
import peer.backend.entity.tag.Tag;
import peer.backend.exception.ConflictException;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.TagRepository;

@RequiredArgsConstructor
@Service
public class TagService {

    private final TagRepository tagRepository;

    @Transactional
    public void createTag(String name, String color) {
        if (this.tagRepository.existsByName(name)) {
            throw new ConflictException("이미 존재하는 Tag 이름입니다!");
        }
        this.tagRepository.save(new Tag(name, color));
    }

    @Transactional
    public void deleteTag(Long tagId) {
        this.tagRepository.deleteById(tagId);
    }

    @Transactional
    public List<Tag> getTagList() {
        return this.tagRepository.findAll();
    }

    @Transactional
    public void updateTag(Long tagId, String name, String color) {
        if (this.tagRepository.existsByName(name)) {
            throw new ConflictException("이미 존재하는 Tag 이름입니다!");
        }
        Tag tag = this.getTag(tagId);
        tag.setName(name);
        tag.setColor(color);
    }

    @Transactional
    public Tag getTag(Long tagId) {
        return this.tagRepository.findById(tagId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 tagId 입니다!"));
    }

    public List<Tag> recruitTagListToTagList(List<RecruitTag> recruitTags) {
        return recruitTags.stream().map(RecruitTag::getTag).collect(Collectors.toList());
    }

    public List<TagResponse> tagListToTagResponseList(List<Tag> tags) {
        return tags.stream().map(TagResponse::new).collect(Collectors.toList());
    }

    public List<TagResponse> recruitTagListToTagResponseList(List<RecruitTag> recruitTagList) {
        return this.tagListToTagResponseList(this.recruitTagListToTagList(recruitTagList));
    }
}
