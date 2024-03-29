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
import peer.backend.dto.noti.enums.NotificationPriority;
import peer.backend.dto.noti.enums.NotificationType;
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
import peer.backend.service.noti.NotificationCreationService;
import peer.backend.service.team.TeamService;

import java.util.List;
import java.util.Objects;
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

    private final NotificationCreationService notificationCreationService;
    private static final String detailPage = "/showcase/detail/";
    private static final String filePath = "team/showcase/";

    private List<UserShowcaseResponse> getMembers(List<TeamUser> teamUsers) {
        return teamUsers.stream()
            .filter(teamUser -> teamUser.getStatus().equals(TeamUserStatus.APPROVED))
            .map(UserShowcaseResponse::new)
            .collect(Collectors.toList());
    }

    private String excludeImageUrlFromContent(String origin) {
        return origin.replaceAll("!\\[.*?\\]\\(.*?\\)", "");
    }

    private ShowcaseListResponse convertToDto(Post post, Authentication auth) {
        Team team = post.getBoard().getTeam();
        return ShowcaseListResponse.builder()
            .id(post.getId())
            .image(post.getImage())     // showcase에서 대표이미지는 항상 첫번째인덱스에 있습니다.
            .name(post.getBoard().getTeam().getName())
            .description(excludeImageUrlFromContent(post.getContent()))
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
        Page<Post> posts = postRepository.findAllByBoardTypeAndIsPublicOrderByCreatedAtDesc(
            BoardType.SHOWCASE,
            true,
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

            // 관심리스트에 추가 됨을 알림
            this.notificationCreationService.makeNotificationForTeam(
                null,
                showcase.getTitle() + "  가 누군가의 관심리스트에 등록되었습니다!",
                detailPage + showcase.getId(),
                NotificationPriority.IMMEDIATE,
                NotificationType.SYSTEM,
                null,
                showcase.getOwnTeamId(),
                null
            );

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

        boolean likeOrHate;

        if (postLike.isPresent()) {
            postLikeRepository.delete(postLike.get());
            showcase.decreaseLike();
            likeOrHate = false;
        } else {
            PostLike newFavorite = new PostLike();
            newFavorite.setUser(user);
            newFavorite.setPost(showcase);
            newFavorite.setUserId(user.getId());
            newFavorite.setPostId(showcaseId);
            newFavorite.setType(PostLikeType.LIKE);
            postLikeRepository.save(newFavorite);
            showcase.increaseLike();
            likeOrHate = true;
        }

        if (likeOrHate) {
            this.notificationCreationService.makeNotificationForTeam(
                null,
                showcase.getTitle() + " 가 좋아요를 받았습니다! 확인해보시겠어요?",
                detailPage + showcase.getId(),
                NotificationPriority.IMMEDIATE,
                NotificationType.SYSTEM,
                null,
                showcase.getOwnTeamId(),
                null
            );
        }

        return showcase.getLiked();
    }

    @Transactional
    public ShowcaseResponse getShowcase(Long showcaseId, Authentication auth) {
        Post showcase = postRepository.findById(showcaseId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 쇼케이스입니다."));
        if (!showcase.getBoard().getType().equals(BoardType.SHOWCASE)) {
            throw new IllegalArgumentException("쇼케이스 게시물이 아닙니다.");
        }
        User user = (auth != null ? User.authenticationToUser(auth) : null);
        Team team = showcase.getBoard().getTeam();
        showcase.increaseHit();
        return ShowcaseResponse.builder()
            .content(showcase.getContent())
            .image(showcase.getImage())
            .start(team.getCreatedAt().toString())
            .end(team.getEnd().toString())
            .likeCount(showcase.getLiked())
            .liked(auth != null && postLikeRepository.findById(
                new PostLikePK(user.getId(), showcaseId, PostLikeType.LIKE)).isPresent())
            .favorite(auth != null && postLikeRepository.findById(
                new PostLikePK(user.getId(), showcaseId, PostLikeType.FAVORITE)).isPresent())
            .author(auth != null && user.getId().equals(showcase.getUser().getId()))
            .name(team.getName())
            .skills(tagService.recruitTagListToTagResponseList(team.getRecruit().getRecruitTags()))
            .member(getMembers(team.getTeamUsers()))
            .links(showcase.getLinks().stream().map(PostLinkResponse::new)
                .collect(Collectors.toList()))
            .build();
    }

    @Transactional
    public ShowcaseWriteResponse getTeamInfoForCreateShowcase(Long teamId, Authentication auth) {
        User user = User.authenticationToUser(auth);
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 팀입니다."));
        if (!teamService.isLeader(teamId, user)) {
            throw new ForbiddenException("리더가 아닙니다.");
        }
        if (!team.getStatus().equals(TeamStatus.COMPLETE)) {
            throw new ConflictException("팀이 종료되지 않았습니다.");
        }
        return new ShowcaseWriteResponse(
            team,
            tagService.recruitTagListToTagList(team.getRecruit().getRecruitTags()),
            team.getTeamUsers());
    }

    //TODO:모듈화 필요
    @Transactional
    public Long createShowcase(ShowcaseCreateDto request, Authentication auth) {
        Team team = teamRepository.findById(request.getTeamId())
            .orElseThrow(() -> new NotFoundException("존재하지 않는 팀입니다."));
        User user = User.authenticationToUser(auth);
        if (!teamService.isLeader(team.getId(), user)) {
            throw new ForbiddenException("리더가 아닙니다.");
        }
        if (postRepository.findByBoardTeamIdAndBoardType(team.getId(), BoardType.SHOWCASE)
            .isPresent()) {
            throw new ConflictException("이미 쇼케이스가 존재합니다.");
        }
        if (!team.getStatus().equals(TeamStatus.COMPLETE)) {
            throw new ConflictException("프로젝트가 종료되지 않았습니다.");
        }
        Board board = Board.builder()
            .team(team)
            .name("쇼케이스")
            .type(BoardType.SHOWCASE)
            .build();
        boardRepository.save(board);
        String path = ShowcaseService.filePath + team.getName();
        Post post = Post.builder()
            .content(request.getContent())
            .liked(0)
            .hit(0)
            .board(board)
            .user(user)
            .title(team.getName() + "'s showcase")
            .ownTeamId(team.getId())
            .image(objectService.uploadObject(path, request.getImage(), "image"))
            .build();

        postRepository.save(post);
        post.addLinks(request.getLinks());
        post.addFiles(objectService.extractContentImage(request.getContent()));

        this.notificationCreationService.makeNotificationForTeam(
            null,
            post.getTitle() + " 가 등록 되었습니다! 한 번 확인하러 가볼까요?",
            detailPage + post.getId(),
            NotificationPriority.IMMEDIATE,
            NotificationType.SYSTEM,
            null,
            team.getId(),
            team.getTeamLogoPath()
        );

        return post.getId();
    }

    @Transactional
    public ResponseEntity<Object> updateShowcase(Long showcaseId, ShowcaseUpdateDto request,
        User user) {
        Post post = postRepository.findById(showcaseId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 게시글입니다."));
        Team team = post.getBoard().getTeam();
        List<String> contentImages = objectService.extractContentImage(request.getContent());
        if (!teamService.isLeader(team.getId(), user)) {
            throw new ForbiddenException("리더가 아닙니다.");
        }
        String path = ShowcaseService.filePath + post.getBoard().getTeam().getName();
        String temp = post.getImage();
        if (Objects.nonNull(request.getImage())) {
            post.update(
                request,
                objectService.uploadObject(path, request.getImage(), "image"),
                contentImages);
            objectService.deleteObject(temp);
        } else {
            post.update(request, null, contentImages);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Object> deleteShowcase(Long showcaseId, User user) {
        Post post = postRepository.findById(showcaseId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 게시글입니다."));
        Team team = post.getBoard().getTeam();
        if (!teamService.isLeader(team.getId(), user)) {
            throw new ForbiddenException("리더가 아닙니다.");
        }
        if (post.getImage() != null) {
            objectService.deleteObject(post.getImage());
        }
        if (post.getFiles() != null && !post.getFiles().isEmpty()) {
            post.getFiles().forEach(file -> objectService.deleteObject(file.getUrl()));
        }
        boardRepository.delete(post.getBoard());
        postRepository.delete(post);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Transactional
    public ShowcasePageInfoResponse getShowcasePageInfo(Long teamId, User user) {
        if (!teamService.isLeader(teamId, user)) {
            throw new ForbiddenException("리더가 아닙니다.");
        }
        Optional<Post> post = postRepository.findByBoardTeamIdAndBoardType(teamId,
            BoardType.SHOWCASE);
        if (post.isEmpty()) {
            return ShowcasePageInfoResponse.builder()
                .isPublihsed(false)
                .isPublic(false)
                .showcaseId(0L)
                .build();
        }
        Post showcase = post.get();
        return ShowcasePageInfoResponse.builder()
            .isPublihsed(true)
            .isPublic(showcase.isPublic())
            .showcaseId(showcase.getId())
            .build();
    }

    @Transactional
    public boolean changeShowcasePublic(Long showcaseId, User user) {
        Post post = postRepository.findById(showcaseId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 쇼케이스입니다."));
        if (!teamService.isLeader(post.getBoard().getTeam().getId(), user)) {
            throw new ForbiddenException("리더가 아닙니다.");
        }
        post.changeIsPublic();
        return post.isPublic();
    }
}