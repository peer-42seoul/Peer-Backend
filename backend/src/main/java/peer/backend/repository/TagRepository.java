package peer.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {

    void deleteByTag(String tag);
}
