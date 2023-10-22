package peer.backend.repository.board.recruit;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.board.recruit.RecruitApplicant;
import peer.backend.entity.composite.RecruitApplicantPK;

import java.util.List;

public interface RecruitApplicantRepository extends JpaRepository<RecruitApplicant, RecruitApplicantPK> {
    List<RecruitApplicant> findByUserId(Long userId);
}
