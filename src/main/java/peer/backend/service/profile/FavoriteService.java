package peer.backend.service.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.dto.profile.response.RecruitFavoriteDto;
import peer.backend.dto.profile.response.ShowcaseFavoriteResponse;
import peer.backend.entity.board.recruit.RecruitFavorite;
import peer.backend.entity.board.recruit.enums.RecruitFavoriteEnum;
import peer.backend.entity.board.team.Post;
import peer.backend.entity.board.team.enums.BoardType;
import peer.backend.entity.board.team.enums.PostLikeType;
import peer.backend.entity.team.enums.TeamType;
import peer.backend.entity.user.User;
import peer.backend.repository.board.recruit.RecruitFavoriteRepository;
import peer.backend.repository.board.team.PostRepository;
import peer.backend.service.TagService;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final RecruitFavoriteRepository recruitFavoriteRepository;
    private final PostRepository postRepository;
    private final TagService tagService;

    @Transactional(readOnly = true)
    public Page<RecruitFavoriteDto> getFavorite(User user, String type, int pageIndex, int pageSize) {
        Page<RecruitFavorite> findRecruitFavorite =
                recruitFavoriteRepository.findByUserIdAndTypeAndRecruitTeamType(
                        user.getId(),
                        RecruitFavoriteEnum.LIKE,
                        TeamType.from(type),
                        PageRequest.of(pageIndex, pageSize));
        return findRecruitFavorite.map(RecruitFavoriteDto::new);
    }

    @Transactional
    public void deleteAll(User user, String type) {
        recruitFavoriteRepository
                .deleteAllByUserIdAndTypeAndRecruitTeamType(
                        user.getId(),
                        RecruitFavoriteEnum.LIKE,
                        TeamType.from(type));
    }

    @Transactional
    public Page<ShowcaseFavoriteResponse> getShowcaseFavorites(User user, Pageable pageable) {
        Page<Post> favorites = postRepository.findShowcaseFavoriteList(
                true,
                BoardType.SHOWCASE,
                user.getId(),
                PostLikeType.FAVORITE,
                pageable
        );
        return favorites.map(favorite ->
                new ShowcaseFavoriteResponse(
                        favorite, tagService.recruitTagListToTagResponseList(
                                favorite.getBoard().getTeam().getRecruit().getRecruitTags()))
        );
    }
}