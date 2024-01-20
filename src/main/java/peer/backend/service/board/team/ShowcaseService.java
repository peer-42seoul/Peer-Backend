package peer.backend.service.board.team;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.dto.board.team.*;
import peer.backend.dto.user.UserShowcaseResponse;
import peer.backend.entity.board.team.Board;
import peer.backend.entity.board.team.Post;
import peer.backend.entity.board.team.PostLike;
import peer.backend.entity.board.team.enums.BoardType;
import peer.backend.entity.board.team.enums.PostLikeType;
import peer.backend.entity.composite.PostLikePK;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.team.enums.TeamStatus;
import peer.backend.entity.team.enums.TeamUserStatus;
import peer.backend.entity.user.User;
import peer.backend.exception.ConflictException;
import peer.backend.exception.ForbiddenException;
import peer.backend.exception.IllegalArgumentException;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.board.team.BoardRepository;
import peer.backend.repository.board.team.PostLikeRepository;
import peer.backend.repository.board.team.PostRepository;
import peer.backend.repository.team.TeamRepository;
import peer.backend.service.TagService;
import peer.backend.service.file.ObjectService;
import peer.backend.service.team.TeamService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShowcaseService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final TagService tagService;
    private final TeamService teamService;
    private final TeamRepository teamRepository;
    private final BoardRepository boardRepository;
    private final ObjectService objectService;

    private List<UserShowcaseResponse> getMembers(List<TeamUser> teamUsers){
        return teamUsers.stream()
                .filter(teamUser -> teamUser.getStatus().equals(TeamUserStatus.APPROVED))
                .map(UserShowcaseResponse::new)
                .collect(Collectors.toList());
    }

    private ShowcaseListResponse convertToDto(Post post, Authentication auth) {
        Team team = post.getBoard().getTeam();
        return ShowcaseListResponse.builder()
            .id(post.getId())
            .image(post.getImage())
            .name(post.getBoard().getTeam().getName())
            .description(post.getContent())
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

        return posts.map(post -> convertToDto(post, auth));
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
        } else {
            PostLike newFavorite = new PostLike();
            newFavorite.setUser(user);
            newFavorite.setPost(showcase);
            newFavorite.setUserId(user.getId());
            newFavorite.setPostId(showcaseId);
            newFavorite.setType(PostLikeType.LIKE);
            postLikeRepository.save(newFavorite);
            showcase.increaseLike();
        }
        return showcase.getLiked();
    }

    @Transactional
    public ShowcaseResponse getShowcase(Long showcaseId, Authentication auth){
        Post showcase = postRepository.findById(showcaseId).orElseThrow(() -> new NotFoundException("존재하지 않는 쇼케이스입니다."));
        if (!showcase.getBoard().getType().equals(BoardType.SHOWCASE))
            throw new IllegalArgumentException("쇼케이스 게시물이 아닙니다.");
        User user = (auth != null ? User.authenticationToUser(auth) : null);
        Team team = showcase.getBoard().getTeam();
        showcase.increaseHit();
        return ShowcaseResponse.builder()
                .content(showcase.getContent())
                .image(showcase.getFiles().get(0).getUrl())
                .start(team.getCreatedAt().toString())
                .end(team.getEnd().toString())
                .likeCount(showcase.getLiked())
                .liked(auth != null && postLikeRepository.findById(new PostLikePK(user.getId(), showcaseId, PostLikeType.LIKE)).isPresent())
                .favorite(auth != null && postLikeRepository.findById(new PostLikePK(user.getId(), showcaseId, PostLikeType.FAVORITE)).isPresent())
                .author(auth != null && user.getId().equals(showcase.getUser().getId()))
                .name(team.getName())
                .skills(tagService.recruitTagListToTagResponseList(team.getRecruit().getRecruitTags()))
                .member(getMembers(team.getTeamUsers()))
                .links(showcase.getLinks().stream().map(PostLinkResponse::new).collect(Collectors.toList()))
                .build();
    }

    @Transactional
    public ShowcaseWriteResponse getTeamInfoForCreateShowcase(Long teamId, Authentication auth){
        User user = User.authenticationToUser(auth);
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 팀입니다."));
        if (!teamService.isLeader(teamId, user))
            throw new ForbiddenException("리더가 아닙니다.");
        return new ShowcaseWriteResponse(
                team,
                tagService.recruitTagListToTagList(team.getRecruit().getRecruitTags()),
                team.getTeamUsers());
    }

    //TODO:모듈화 필요
    @Transactional
    public Long createShowcase(ShowcaseCreateDto request, Authentication auth){
        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 팀입니다."));
        User user = User.authenticationToUser(auth);
        if (!teamService.isLeader(team.getId(), user))
            throw new ForbiddenException("리더가 아닙니다.");
        if (postRepository.findByBoardTeamIdAndBoardType(team.getId(), BoardType.SHOWCASE).isPresent())
            throw new ConflictException("이미 쇼케이스가 존재합니다.");
        if (!team.getStatus().equals(TeamStatus.COMPLETE))
            throw new ConflictException("프로젝트가 종료되지 않았습니다.");
        Board board = Board.builder()
                .team(team)
                .name("쇼케이스")
                .type(BoardType.SHOWCASE)
                .build();
        boardRepository.save(board);
        Post post = Post.builder()
                .content(request.getContent())
                .liked(0)
                .hit(0)
                .board(board)
                .user(user)
                .title(team.getName() + "'s showcase")
                .build();
        String filePath = "team/showcase/" + team.getName();
        postRepository.save(post);
        post.addLinks(request.getLinks());
        post.addFile(objectService.uploadObject(filePath, request.getImage(), "image"));
        return post.getId();
    }

    @Transactional
    public ResponseEntity<Object> updateShowcase(Long showcaseId, ShowcaseUpdateDto request, User user){
        Post post = postRepository.findById(showcaseId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 게시글입니다."));
        Team team = post.getBoard().getTeam();
        if (!teamService.isLeader(team.getId(), user))
            throw new ForbiddenException("리더가 아닙니다.");
        String filePath = "team/showcase/" + post.getBoard().getTeam().getName();
        String temp = post.getFiles().get(0).getUrl();
        post.update(
                request,
                objectService.uploadObject(filePath, request.getImage(), "image"));
        objectService.deleteObject(temp);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Object> deleteShowcase(Long showcaseId, User user){
        Post post = postRepository.findById(showcaseId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 게시글입니다."));
        Team team = post.getBoard().getTeam();
        if (!teamService.isLeader(team.getId(), user))
            throw new ForbiddenException("리더가 아닙니다.");
        objectService.deleteObject(post.getFiles().get(0).getUrl());
        boardRepository.delete(post.getBoard());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}