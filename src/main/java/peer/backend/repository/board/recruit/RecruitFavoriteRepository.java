package peer.backend.repository.board.recruit;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.board.recruit.RecruitFavorite;
import peer.backend.entity.board.recruit.enums.RecruitFavoriteEnum;
import peer.backend.entity.composite.RecruitFavoritePK;

public interface RecruitFavoriteRepository extends JpaRepository<RecruitFavorite, RecruitFavoritePK> {
    boolean existsByUserIdAndRecruitIdAndType(Long userId, Long recruitId, RecruitFavoriteEnum type);
}
