package peer.backend.service.board.team;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.dto.board.team.ShowcaseListResponse;
import peer.backend.entity.board.recruit.TagListManager;
import peer.backend.entity.board.team.Post;
import peer.backend.entity.board.team.PostLike;
import peer.backend.entity.board.team.enums.BoardType;
import peer.backend.entity.board.team.enums.PostLikeType;
import peer.backend.entity.composite.PostLikePK;
import peer.backend.entity.team.Team;
import peer.backend.entity.user.User;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.board.team.PostLikeRepository;
import peer.backend.repository.board.team.PostRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowcaseService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    private ShowcaseListResponse convertToDto(Post post, Authentication auth){
        Team team = post.getBoard().getTeam();
        return ShowcaseListResponse.builder()
                .id(post.getId())
                .image(post.getImage())
                .name(post.getBoard().getTeam().getName())
                .description(post.getContent())
                .skill(TagListManager.getRecruitTags(team.getRecruit().getTags()))
                .like(post.getLiked())
                .isLiked(auth != null && postLikeRepository.findById(new PostLikePK(User.authenticationToUser(auth).getId(), post.getId(), PostLikeType.LIKE)).isPresent())
                .isFavorite(auth != null && postLikeRepository.findById(new PostLikePK(User.authenticationToUser(auth).getId(), post.getId(), PostLikeType.FAVORITE)).isPresent())
                .teamLogo(team.getTeamLogoPath())
                .start(post.getCreatedAt().toString())
                .end(team.getEnd().toString())
                .build();
    }


    @Transactional
    public Page<ShowcaseListResponse> getShowCaseList(int page, int pageSize, Authentication auth){
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Post> posts = postRepository.findAllByBoardTypeOrderByCreatedAtDesc(BoardType.SHOWCASE, pageable);
        List<ShowcaseListResponse> postDtoList = new ArrayList<>();
        for (Post post : posts.getContent()) {
            postDtoList.add(convertToDto(post, auth));
        }
        return new PageImpl<>(postDtoList, pageable, posts.getTotalElements());
    }

    @Transactional
    public void doFavorite(Long showcaseId, Authentication auth){
        User user = User.authenticationToUser(auth);
        Post showcase = postRepository.findById(showcaseId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 모집글입니다."));
        postLikeRepository.findById(new PostLikePK(user.getId(), showcaseId, PostLikeType.FAVORITE))
                .ifPresentOrElse(postLikeRepository::delete,
                        () -> {
                            PostLike newFavorite = new PostLike();
                            newFavorite.setUser(user);
                            newFavorite.setPost(showcase);
                            newFavorite.setUserId(user.getId());
                            newFavorite.setPostId(showcaseId);
                            newFavorite.setType(PostLikeType.FAVORITE);
                            postLikeRepository.save(newFavorite);
                        });
    }

    @Transactional
    public void doLike(Long showcaseId, Authentication auth){
        User user = User.authenticationToUser(auth);
        Post showcase = postRepository.findById(showcaseId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 모집글입니다."));
        postLikeRepository.findById(new PostLikePK(user.getId(), showcaseId, PostLikeType.LIKE))
                .ifPresentOrElse( favorite -> { postLikeRepository.delete(favorite); showcase.decreaseLike(); } ,
                        () -> {
                            showcase.increaseLike();
                            PostLike newFavorite = new PostLike();
                            newFavorite.setUser(user);
                            newFavorite.setPost(showcase);
                            newFavorite.setUserId(user.getId());
                            newFavorite.setPostId(showcaseId);
                            newFavorite.setType(PostLikeType.LIKE);
                            postLikeRepository.save(newFavorite);
                        });
    }
}
