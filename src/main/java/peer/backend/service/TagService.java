package peer.backend.service;

import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import peer.backend.dto.tag.TagResponse;
import peer.backend.entity.tag.RecruitTag;
import peer.backend.entity.tag.Tag;
import peer.backend.exception.ConflictException;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.TagRepository;
import peer.backend.repository.board.recruit.RecruitTagRepository;

@RequiredArgsConstructor
@Service
@Slf4j
public class TagService {

    private final TagRepository tagRepository;
    private final RecruitTagRepository recruitTagRepository;

    @Transactional
    public void createTag(String name, String color) {
        try {
            this.tagRepository.saveAndFlush(new Tag(name, color));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("이미 존재하는 Tag 이름입니다!");
        }
    }

    @Transactional
    public void deleteTag(Long tagId) {
        this.recruitTagRepository.deleteAllByTagId(tagId);
        this.tagRepository.deleteById(tagId);
    }

    @Transactional
    public List<Tag> getTagList() {
        return this.tagRepository.findAll();
    }

    @Transactional
    public void updateTag(Long tagId, String name, String color) {
        try {
            Tag tag = this.getTag(tagId);
            tag.setName(name);
            tag.setColor(color);
            this.tagRepository.saveAndFlush(tag);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("이미 존재하는 Tag 이름입니다!");
        }
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
