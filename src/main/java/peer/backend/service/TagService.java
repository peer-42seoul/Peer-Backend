package peer.backend.service;

import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import peer.backend.entity.Tag;
import peer.backend.exception.ConflictException;
import peer.backend.repository.TagRepository;

@RequiredArgsConstructor
@Service
public class TagService {

    private final TagRepository tagRepository;

    @Transactional
    public void insertTag(String name, String color) {
        if (tagRepository.existsByName(name)) {
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
}
