package peer.backend.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import peer.backend.entity.tag.Tag;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {

    boolean existsByName(String name);

    @Query("SELECT m FROM Tag m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Tag> findAllByTagName(@Param("keyword") String keyword);

    @Query("SELECT m FROM Tag m WHERE m.id IN :ids")
    List<Tag> findAllByIdIn(@Param("ids")List<Long> ids);
}
