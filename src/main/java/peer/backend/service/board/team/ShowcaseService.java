package peer.backend.service.board.team;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.dto.board.team.ShowcaseListResponse;
import peer.backend.dto.board.team.ShowcaseResponse;
import peer.backend.entity.board.team.Post;
import peer.backend.entity.board.team.PostLike;
import peer.backend.entity.board.team.enums.BoardType;
import peer.backend.entity.board.team.enums.PostLikeType;
import peer.backend.entity.composite.PostLikePK;
import peer.backend.entity.team.Team;
import peer.backend.entity.user.User;
import peer.backend.exception.IllegalArgumentException;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.board.team.PostLikeRepository;
import peer.backend.repository.board.team.PostRepository;
import peer.backend.service.TagService;

@Service
@RequiredArgsConstructor
public class ShowcaseService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final TagService tagService;

    private ShowcaseListResponse convertToDto(Post post, Authentication auth) {
        Team team = post.getBoard().getTeam();
        return ShowcaseListResponse.builder()
            .id(post.getId())
            .image(post.getImage())
            .name(post.getBoard().getTeam().getName())
            .description(post.getContent())
//            .skill(TagListManager.getRecruitTags(team.getRecruit().getRecruitTags()))
            .skill(
                this.tagService.recruitTagListToTagResponseList(team.getRecruit().getRecruitTags()))
            .like(post.getLiked())
            .isLiked(auth != null && postLikeRepository.findById(
                new PostLikePK(User.authenticationToUser(auth).getId(), post.getId(),
                    PostLikeType.LIKE)).isPresent())
            .isFavorite(auth != null && postLikeRepository.findById(
                new PostLikePK(User.authenticationToUser(auth).getId(), post.getId(),
                    PostLikeType.FAVORITE)).isPresent())
            .teamLogo(team.getTeamLogoPath())
            .start(post.getCreatedAt().toString())
            .end(team.getEnd().toString())
            .build();
    }


    @Transactional
    public Page<ShowcaseListResponse> getShowCaseList(int page, int pageSize, Authentication auth) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Post> posts = postRepository.findAllByBoardTypeOrderByCreatedAtDesc(BoardType.SHOWCASE,
            pageable);
        List<ShowcaseListResponse> postDtoList = new ArrayList<>();
        for (Post post : posts.getContent()) {
            postDtoList.add(convertToDto(post, auth));
        }
        return new PageImpl<>(postDtoList, pageable, posts.getTotalElements());
    }

    @Transactional
    public boolean doFavorite(Long showcaseId, Authentication auth) {
        User user = User.authenticationToUser(auth);
        Post showcase = postRepository.findById(showcaseId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 쇼케이스입니다."));
        Optional<PostLike> postLike = postLikeRepository.findById(
            new PostLikePK(user.getId(), showcaseId, PostLikeType.FAVORITE));
        if (postLike.isPresent()) {
            postLikeRepository.delete(postLike.get());
            return false;
        } else {
            PostLike newFavorite = new PostLike();
            newFavorite.setUser(user);
            newFavorite.setPost(showcase);
            newFavorite.setUserId(user.getId());
            newFavorite.setPostId(showcaseId);
            newFavorite.setType(PostLikeType.FAVORITE);
            postLikeRepository.save(newFavorite);
            return true;
        }
    }

    @Transactional
    public int doLike(Long showcaseId, Authentication auth) {
        User user = User.authenticationToUser(auth);
        Post showcase = postRepository.findById(showcaseId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 모집글입니다."));
        Optional<PostLike> postLike = postLikeRepository.findById(
            new PostLikePK(user.getId(), showcaseId, PostLikeType.LIKE));
        if (postLike.isPresent()) {
            postLikeRepository.delete(postLike.get());
            showcase.decreaseLike();
            return showcase.getLiked();
        } else {
            PostLike newFavorite = new PostLike();
            newFavorite.setUser(user);
            newFavorite.setPost(showcase);
            newFavorite.setUserId(user.getId());
            newFavorite.setPostId(showcaseId);
            newFavorite.setType(PostLikeType.LIKE);
            postLikeRepository.save(newFavorite);
            showcase.increaseLike();
            return showcase.getLiked();
        }
    }

    public

    @Transactional
    public ShowcaseResponse getShowcase(Long showcaseId, Authentication auth){
        Post showcase = postRepository.findById(showcaseId).orElseThrow(() -> new NotFoundException("존재하지 않는 쇼케이스입니다."));
        if (!showcase.getBoard().getType().equals(BoardType.SHOWCASE))
            throw new IllegalArgumentException("쇼케이스 게시물이 아닙니다.");
        User user = User.authenticationToUser(auth)
        Team team = showcase.getBoard().getTeam();
        return ShowcaseResponse.builder()
                .content(showcase.getContent())
                .image(showcase.getImage())
                .start(team.getCreatedAt())
                .end(team.getEnd())
                .likeCount(showcase.getLiked())
                .liked(auth != null && postLikeRepository.findById(new PostLikePK(user.getId(), showcaseId, PostLikeType.LIKE)).isPresent())
                .favorite(auth != null && postLikeRepository.findById(new PostLikePK(user.getId(), showcaseId, PostLikeType.FAVORITE)).isPresent())
                .author(user.getId().equals(showcase.getUser().getId()))
                .

    }
}
