package peer.backend.service.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.dto.profile.FavoritePage;
import peer.backend.dto.profile.response.FavoriteResponse;
import peer.backend.entity.board.recruit.Recruit;
import peer.backend.entity.board.recruit.RecruitFavorite;
import peer.backend.entity.board.recruit.TagListManager;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.team.enums.TeamType;
import peer.backend.entity.team.enums.TeamUserRoleType;
import peer.backend.entity.user.User;
import peer.backend.exception.NotFoundException;
import peer.backend.oauth.PrincipalDetails;
import peer.backend.repository.board.recruit.RecruitFavoriteRepository;
import peer.backend.repository.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final UserRepository userRepository;
    private final RecruitFavoriteRepository recruitFavoriteRepository;
    @PersistenceContext
    private EntityManager em;

    private User getLeader(Recruit recruit) {
        List<TeamUser> teamUserList = recruit.getTeam().getTeamUsers();
        for (TeamUser teamUser : teamUserList) {
            if (teamUser.getRole().equals(TeamUserRoleType.LEADER)) {
                return teamUser.getUser();
            }
        }
        return null;
    }

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

    @Transactional(readOnly = true)
    public FavoritePage getFavorite(Authentication auth, String type, int pageIndex, int pageSize) {
        User user = User.authenticationToUser(auth);
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        List<RecruitFavorite> recruitFavoriteList = recruitFavoriteRepository.findAllByUserId(user.getId());
        List<FavoriteResponse> favoriteResponseList = new ArrayList<>();
        if (recruitFavoriteList != null) {
            for (RecruitFavorite recruitFavorite : recruitFavoriteList) {
                Recruit recruit = recruitFavorite.getRecruit();
                String teamType = recruit.getTeam().getType().equals(TeamType.PROJECT) ? "PROJECT" : "STUDY";
                if (teamType.equals(type)) {
                    User leader = getLeader(recruit);
                    FavoriteResponse favoriteResponse = FavoriteResponse.builder()
                            .recruit_id(recruit.getId())
                            .title(recruit.getTitle())
                            .image(recruit.getThumbnailUrl())
                            .userId(leader != null ? leader.getId() : -1)
                            .userNickname(leader != null ? leader.getNickname() : null)
                            .userImage(leader != null ? leader.getImageUrl() : null)
                            .status(recruit.getStatus().getStatus())
                            .tagList(TagListManager.getRecruitTags(recruit.getTags()))
                            .build();
                    favoriteResponseList.add(favoriteResponse);
                }
            }
        }
        return new FavoritePage(favoriteResponseList, pageable);
    }

    @Transactional
    public void deleteAll(Authentication auth, String type) {
        User user = User.authenticationToUser(auth);
        List<RecruitFavorite> recruitFavoriteList = recruitFavoriteRepository.findAllByUserId(user.getId());
        List<RecruitFavorite> toDelete = new ArrayList<>();
        for (RecruitFavorite recruitFavorite : recruitFavoriteList) {
            Recruit recruit = recruitFavorite.getRecruit();
            String teamType = recruit.getTeam().getType().equals(TeamType.PROJECT) ? "PROJECT" : "STUDY";
            if (teamType.equals(type)) {
                toDelete.add(recruitFavorite);
            }
        }
        for (RecruitFavorite recruitFavorite : toDelete) {
            recruitFavoriteList.remove(recruitFavorite);
        }
        userRepository.save(user);
    }
}