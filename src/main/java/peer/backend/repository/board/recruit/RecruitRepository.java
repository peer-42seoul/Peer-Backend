package peer.backend.repository.board.recruit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import peer.backend.entity.board.recruit.Recruit;
import peer.backend.entity.board.recruit.enums.RecruitStatus;
import peer.backend.entity.team.enums.TeamType;
import peer.backend.entity.user.User;

public interface RecruitRepository extends JpaRepository<Recruit, Long> {
    @Query("SELECT r FROM Recruit r JOIN r.team t WHERE t.type = :type AND r.status = :status AND (:userId IS NULL OR NOT EXISTS (SELECT f FROM RecruitFavorite f WHERE f.recruit = r AND f.user.id = :userId))")
    Page<Recruit> findAllByStatusAndTeamTypeAndFavorite(RecruitStatus status, TeamType type, Long userId, Pageable pageable);
}
