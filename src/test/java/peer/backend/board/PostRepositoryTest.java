package peer.backend.board;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import peer.backend.entity.board.recruit.Recruit;
import peer.backend.entity.board.recruit.enums.RecruitDueEnum;
import peer.backend.entity.board.recruit.enums.RecruitStatus;
import peer.backend.entity.board.team.Board;
import peer.backend.entity.board.team.Post;
import peer.backend.entity.board.team.enums.BoardType;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.enums.TeamMemberStatus;
import peer.backend.entity.team.enums.TeamOperationFormat;
import peer.backend.entity.team.enums.TeamStatus;
import peer.backend.entity.team.enums.TeamType;
import peer.backend.entity.user.User;
import peer.backend.repository.board.team.PostRepository;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Team Repository 테스트")
@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest
public class PostRepositoryTest {
    @Autowired
    PostRepository postRepository;

    Post post;
    User user;
    Board board;
    Team team;
    Recruit recruit;


    @BeforeEach
    void beforeEach() {
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
    }

    @Test
    @DisplayName("Team Repository insert test")
    void insertTest() {
        postRepository.deleteAll();
        assertEquals(postRepository.count(), 0);
        postRepository.save(post);
        assertEquals(postRepository.count(), 1);
    }
}
