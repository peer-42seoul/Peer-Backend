package peer.backend.service;

import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import peer.backend.entity.Tag;
import peer.backend.repository.TagRepository;

@RequiredArgsConstructor
@Service
public class TagService {

    private final TagRepository tagRepository;

    @Transactional
    public void insertTag(String name, String color) {
        this.tagRepository.save(new Tag(name, color));
    }

    @Transactional
    public void deleteTag(String name) {
        this.tagRepository.deleteByName(name);
    }

    @Transactional
    public List<Tag> getTagList() {
        return this.tagRepository.findAll();
    }
}
