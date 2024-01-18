package peer.backend.repository.board.recruit;

import java.util.List;
import javax.transaction.Transactional;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import peer.backend.entity.tag.RecruitTag;

public interface RecruitTagRepository extends JpaRepository<RecruitTag, Long> {

    List<RecruitTag> findAllByTagId(Long id);

    @Modifying
    @Transactional
    @Query("DELETE FROM RecruitTag rt WHERE rt.tagId = :tagId")
    void deleteAllByTagId(Long tagId);

    @Query("SELECT m FROM RecruitTag m WHERE m.recruitId IN :recruitIds")
    List<RecruitTag> findByRecruitIdIn(@Param("recruitIds") List<Long> recruitIds);
}
