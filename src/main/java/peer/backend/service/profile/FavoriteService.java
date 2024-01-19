package peer.backend.service.profile;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.dto.board.recruit.RecruitListResponse;
import peer.backend.dto.profile.FavoritePage;
import peer.backend.dto.profile.response.RecruitFavoriteDto;
import peer.backend.entity.board.recruit.Recruit;
import peer.backend.entity.board.recruit.RecruitFavorite;
import peer.backend.entity.board.recruit.enums.RecruitFavoriteEnum;
import peer.backend.entity.team.enums.TeamType;
import peer.backend.entity.user.User;
import peer.backend.repository.board.recruit.RecruitFavoriteRepository;
import peer.backend.repository.board.recruit.RecruitRepository;
import peer.backend.service.TagService;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final RecruitFavoriteRepository recruitFavoriteRepository;

    @Transactional(readOnly = true)
    public Page<RecruitFavoriteDto> getFavorite(Authentication auth, String type, int pageIndex, int pageSize) {
        User user = User.authenticationToUser(auth);
        Page<RecruitFavorite> findRecruitFavorite =
                recruitFavoriteRepository.findByUserIdAndTypeAndRecruitTeamType(
                        user.getId(),
                        RecruitFavoriteEnum.LIKE,
                        TeamType.from(type),
                        PageRequest.of(pageIndex, pageSize));
        return findRecruitFavorite.map(favorite -> new RecruitFavoriteDto(favorite, user));
    }

    @Transactional
    public void deleteAll(Authentication auth, String type) {
        User user = User.authenticationToUser(auth);
        recruitFavoriteRepository
                .deleteAllByUserIdAndTypeAndRecruitTeamType(
                        user.getId(),
                        RecruitFavoriteEnum.LIKE,
                        TeamType.from(type));
    }
}