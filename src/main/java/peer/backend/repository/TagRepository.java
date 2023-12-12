package peer.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.tag.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {

    boolean existsByName(String name);
}
