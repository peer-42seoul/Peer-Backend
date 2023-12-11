package peer.backend.service.profile;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.dto.board.recruit.RecruitListResponse;
import peer.backend.dto.profile.FavoritePage;
import peer.backend.entity.board.recruit.Recruit;
import peer.backend.entity.board.recruit.RecruitFavorite;
import peer.backend.entity.team.enums.TeamType;
import peer.backend.entity.user.User;
import peer.backend.service.TagService;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final TagService tagService;

    @PersistenceContext
    private EntityManager em;

    private List<Recruit> findAllBy(Long userId, TeamType type) {
        // SELECT * FROM user
        // JOIN recruit_favorite ON user.id = recruit_favorite.user_id
        // JOIN recruit ON recruit_favorite.recruit_id = recruit.recruit_id
        // WHERE user.id = ? AND recruit.type = ?
        return em.createQuery(
                "SELECT r FROM User u " +
                    "JOIN RecruitFavorite rf ON u.id = rf.user.id " +
                    "JOIN Recruit r ON rf.recruit.id = r.id " +
                    "WHERE u.id = :userId AND r.type = :teamType", Recruit.class)
            .setParameter("userId", userId)
            .setParameter("teamType", type)
            .getResultList();
    }

    private List<Recruit> pagingBy(List<Recruit> retFind, int pageIndex, int pageSize) {
        int first = (pageIndex - 1) * pageSize;
        int last = pageIndex * pageSize;
        if (retFind.size() < first) {
            return new ArrayList<>();
        }
        if (retFind.size() < pageSize) {
            return retFind;
        }
        return retFind.subList(first, last);
    }

    @Transactional(readOnly = true)
    public FavoritePage getFavorite(Authentication auth, String type, int pageIndex, int pageSize) {
        User user = User.authenticationToUser(auth);
        List<Recruit> retFind = findAllBy(user.getId(), TeamType.valueOf(type));
        List<Recruit> retPage = pagingBy(retFind, pageIndex, pageSize);
        List<RecruitListResponse> ret = new ArrayList<>();
        for (Recruit recruit : retPage) {
            RecruitListResponse recruitListResponse = RecruitListResponse.builder()
                .recruit_id(recruit.getId())
                .title(recruit.getTitle())
                .image(recruit.getThumbnailUrl())
                .user_id(recruit.getWriter() != null ? recruit.getWriterId() : -1)
                .user_nickname(recruit.getWriter() != null ? recruit.getWriter().getNickname() : "")
                .user_thumbnail(
                    recruit.getWriter() != null ? recruit.getWriter().getImageUrl() : null)
                .status(recruit.getStatus().getStatus())
//                .tagList(TagListManager.getRecruitTags(recruit.getRecruitTags()))
                .tagList(this.tagService.recruitTagListToTagResponseList(recruit.getRecruitTags()))
                .isFavorite(true)
                .build();
            ret.add(recruitListResponse);
        }
        return FavoritePage.builder()
            .postList(ret)
            .isLast(ret.isEmpty())
            .build();
    }

    @Transactional
    public void deleteAll(Authentication auth, String type) {
        User user = User.authenticationToUser(auth);
        List<RecruitFavorite> toDelete = em.createQuery(
                "SELECT rf FROM User u " +
                    "JOIN RecruitFavorite rf ON u.id = rf.user.id " +
                    "JOIN Recruit r ON rf.recruit.id = r.id " +
                    "WHERE u.id = :userId AND r.type = :teamType", RecruitFavorite.class)
            .setParameter("userId", user.getId())
            .setParameter("teamType", TeamType.valueOf(type))
            .getResultList();
        for (RecruitFavorite recruitFavorite : toDelete) {
            em.remove(recruitFavorite);
        }
    }
}