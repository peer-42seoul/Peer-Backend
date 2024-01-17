package peer.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import peer.backend.entity.tag.Tag;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {

    boolean existsByName(String name);

    @Query("SELECT m FROM Tag m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Tag> findAllByTagName(String keyword);

}
