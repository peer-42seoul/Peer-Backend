package peer.backend.board;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import peer.backend.dto.profile.response.PersonalInfoResponse;
import peer.backend.entity.board.recruit.Recruit;
import peer.backend.entity.board.recruit.enums.RecruitDueEnum;
import peer.backend.entity.board.recruit.enums.RecruitStatus;
import peer.backend.entity.board.team.Board;
import peer.backend.entity.board.team.Post;
import peer.backend.entity.board.team.PostLike;
import peer.backend.entity.board.team.enums.BoardType;
import peer.backend.entity.board.team.enums.PostLikeType;
import peer.backend.entity.composite.PostLikePK;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.enums.TeamMemberStatus;
import peer.backend.entity.team.enums.TeamOperationFormat;
import peer.backend.entity.team.enums.TeamStatus;
import peer.backend.entity.team.enums.TeamType;
import peer.backend.entity.user.User;
import peer.backend.oauth.PrincipalDetails;
import peer.backend.repository.board.recruit.RecruitRepository;
import peer.backend.repository.board.team.PostLikeRepository;
import peer.backend.repository.board.team.PostRepository;
import peer.backend.service.board.team.ShowcaseService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ShowcaseService Test")
public class ShowcaseServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private PostLikeRepository postLikeRepository;
    @InjectMocks
    private ShowcaseService showcaseService;


        User user;
        Team team;
        Post post;
        Board board;
        Recruit recruit;
        Authentication auth;
        Post mockPost;

        List<Post> posts = new ArrayList<>();
    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .id(1L)
                .name("test")
                .email("test@test.com")
                .nickname("test")
                .isAlarm(false)
                .address("test")
                .certification(false)
                .company("test")
                .introduce("test")
                .peerLevel(0L)
                .representAchievement("test")
                .build();

        recruit = Recruit.builder()
                .id(1L)
                .link("t")
                .due(RecruitDueEnum.EIGHT_MONTHS)
                .content("t")
                .place(TeamOperationFormat.ONLINE)
                .region1("t")
                .region2("t")
                .status(RecruitStatus.BEFORE)
                .team(team)
                .thumbnailUrl("t")
                .title("t")
                .tags(new ArrayList<>())
                .type(TeamType.PROJECT)
                .build();

        team = Team.builder()
                .id(1L)
                .name("test")
                .type(TeamType.STUDY)
                .dueTo("10월")
                .operationFormat(TeamOperationFormat.ONLINE)
                .status(TeamStatus.ONGOING)
                .teamMemberStatus(TeamMemberStatus.RECRUITING)
                .isLock(false)
                .recruit(recruit)
                .region1("test")
                .region2("test")
                .region3("test")
                .end(LocalDateTime.now())
                .build();

        board = Board.builder()
                .id(1L)
                .name("쇼케이스")
                .type(BoardType.SHOWCASE)
                .team(team)
                .build();

        post = Post.builder()
                .id(1L)
                .content("abcde")
                .image(null)
                .hit(0)
                .liked(0)
                .title("12345")
                .user(user)
                .board(board)
                .build();
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        PrincipalDetails details = new PrincipalDetails(user);
        auth = new UsernamePasswordAuthenticationToken(details, details.getPassword(),
                details.getAuthorities());

        posts.add(post);

        mockPost = mock(Post.class);

    }

    @Test
    @DisplayName("쇼케이스 리스트 가져오기 테스티")
    public void getShowCaseListTest() {
        when(postRepository.findAllByBoardTypeOrderByCreatedAtDesc(any(), any()))
                .thenReturn(new PageImpl<>(posts, PageRequest.of(0, 2), 1L));

        assertThat(showcaseService.getShowCaseList(0, 1, auth).getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("쇼케이스 담기 테스트")
    public void doFavoriteTestWhenPostLikeNotExist() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        showcaseService.doFavorite(1L, auth);
        verify(postLikeRepository).save(any(PostLike.class));
    }

    @Test
    @DisplayName("쇼케이스 담기 테스트")
    public void doFavoriteTestWhenPostLikeExist() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        PostLike mockPostLike = new PostLike();
        when(postLikeRepository.findById(any())).thenReturn(Optional.of(mockPostLike));
        showcaseService.doFavorite(1L, auth);
        verify(postLikeRepository).delete(mockPostLike);
    }

    @Test
    @DisplayName("쇼케이스 좋아요 테스트")
    public void dolikeTestWhenPostLikeExist() {
        when(postRepository.findById(any())).thenReturn(Optional.of(mockPost)); // 이 부분 추가
        PostLike mockPostLike = new PostLike();
        when(postLikeRepository.findById(any())).thenReturn(Optional.of(mockPostLike));
        showcaseService.doLike(1L, auth);
        verify(mockPost).decreaseLike(); // 수정된 부분
    }

}
