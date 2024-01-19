package peer.backend.repository.board.recruit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.board.recruit.RecruitFavorite;
import peer.backend.entity.board.recruit.enums.RecruitFavoriteEnum;
import peer.backend.entity.composite.RecruitFavoritePK;
import peer.backend.entity.team.enums.TeamType;

import java.util.List;

public interface RecruitFavoriteRepository extends JpaRepository<RecruitFavorite, RecruitFavoritePK> {
    boolean existsByUserIdAndRecruitIdAndType(Long userId, Long recruitId, RecruitFavoriteEnum type);

    void deleteAllByUserIdAndTypeAndRecruitTeamType(Long userId, RecruitFavoriteEnum type, TeamType teamType);
    List<RecruitFavorite> findByUserIdAndTypeAndRecruitTeamType(Long userId, RecruitFavoriteEnum type, TeamType teamType);
    Page<RecruitFavorite> findByUserIdAndTypeAndRecruitTeamType(Long userId, RecruitFavoriteEnum type, TeamType teamType, Pageable pageable);
}
