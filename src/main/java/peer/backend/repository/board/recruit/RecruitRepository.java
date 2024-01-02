package peer.backend.repository.board.recruit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.board.recruit.Recruit;
import peer.backend.entity.board.recruit.enums.RecruitStatus;

public interface RecruitRepository extends JpaRepository<Recruit, Long> {
    Page<Recruit> findAllByStatus(RecruitStatus status, Pageable pageable);
}
