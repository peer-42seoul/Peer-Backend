package peer.backend.repository.board.recruit;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.board.recruit.Recruit;

public interface RecruitRepository extends JpaRepository<Recruit, Long> {
}
