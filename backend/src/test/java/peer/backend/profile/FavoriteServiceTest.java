package peer.backend.profile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import peer.backend.dto.profile.FavoritePage;
import peer.backend.dto.profile.response.FavoriteResponse;
import peer.backend.entity.board.recruit.Recruit;
import peer.backend.entity.board.recruit.RecruitFavorite;
import peer.backend.entity.board.recruit.enums.RecruitStatus;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.team.enums.TeamType;
import peer.backend.entity.team.enums.TeamUserRoleType;
import peer.backend.entity.user.User;
import peer.backend.oauth.PrincipalDetails;
import peer.backend.repository.user.UserRepository;
import peer.backend.service.profile.FavoriteService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test FavoriteService")
public class FavoriteServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private FavoriteService favoriteService;

    String name;
    User user;
    List<RecruitFavorite> recruitFavoriteList;
    List<TeamUser> teamUserList;

    @BeforeEach
    void beforeEach() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        name = "test name";
        user = User.builder()
            .id(20L)
            .email("test@email.com")
            .password(encoder.encode("test password"))
            .name(name)
            .nickname("test nickname")
            .isAlarm(false)
            .address("test address")
            .imageUrl("test image URL")
            .build();
        recruitFavoriteList = new ArrayList<>();
        teamUserList = new ArrayList<>();
        for (int index = 0; index < 12; index++) {
            User newUser = User.builder()
                .id((long) index + 1)
                .name("test " + index)
                .nickname("test " + index)
                .imageUrl("test " + index)
                .build();
            TeamUser teamUser = TeamUser.builder()
                .user(newUser)
                .role(index % 4 == 0 ? TeamUserRoleType.LEADER : TeamUserRoleType.MEMBER)
                .build();
            teamUserList.add(teamUser);
        }
        for (int index = 0; index < 3; index++) {
            Team team = Team.builder()
                .teamUsers(teamUserList.subList(index * 4, index * 4 + 3))
                .type(index % 2 == 0 ? TeamType.PROJECT : TeamType.STUDY)
                .build();
            RecruitStatus status;
            if (index == 0) {
                status = RecruitStatus.BEFORE;
            } else if (index == 1) {
                status = RecruitStatus.ONGOING;
            } else {
                status = RecruitStatus.DONE;
            }
            Recruit recruit = Recruit.builder()
                .id((long) index + 1)
                .team(team)
                .title("test title " + index)
                .link("test link " + index)
                .thumbnailUrl("test image " + index)
                .status(status)
                .build();
            RecruitFavorite recruitFavorite = new RecruitFavorite(
                user.getId(), recruit.getId(), user, recruit
            );
            recruitFavoriteList.add(recruitFavorite);
        }
        user.setRecruitFavorites(recruitFavoriteList);
    }

    @Test
    @DisplayName("test get favorite")
    public void getFavoriteTest() {
        FavoritePage ret = favoriteService.getFavorite(user, "project", 1, 10);
        String json;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            json = objectMapper.writeValueAsString(ret);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println(json);
        List<FavoriteResponse> favoriteResponseList = new ArrayList<>();
        for (RecruitFavorite recruitFavorite : recruitFavoriteList) {
            Recruit recruit = recruitFavorite.getRecruit();
            User leader = null;
            for (TeamUser teamUser : recruit.getTeam().getTeamUsers()) {
                if (teamUser.getRole().equals(TeamUserRoleType.LEADER)) {
                    leader = teamUser.getUser();
                }
            }
            if (recruit.getTeam().getType().equals(TeamType.PROJECT)) {
                FavoriteResponse favoriteResponse = FavoriteResponse.builder()
                    .postId(recruit.getId())
                    .title(recruit.getTitle())
                    .image(recruit.getThumbnailUrl())
                    .userId(leader != null ? leader.getId() : -1)
                    .userNickname(leader != null ? leader.getNickname() : null)
                    .userImage(leader != null ? leader.getImageUrl() : null)
                    .tagList(recruit.getTags())
                    .build();
                favoriteResponseList.add(favoriteResponse);
            }
        }
        for (int index = 0; index < ret.getPostList().size(); index++) {
            assertThat(ret.getPostList().get(index).getPostId()).isEqualTo(
                favoriteResponseList.get(index).getPostId());
            assertThat(ret.getPostList().get(index).getTitle()).isEqualTo(
                favoriteResponseList.get(index).getTitle());
            assertThat(ret.getPostList().get(index).getImage()).isEqualTo(
                favoriteResponseList.get(index).getImage());
            assertThat(ret.getPostList().get(index).getUserId()).isEqualTo(
                favoriteResponseList.get(index).getUserId());
            assertThat(ret.getPostList().get(index).getUserNickname()).isEqualTo(
                favoriteResponseList.get(index).getUserNickname());
            assertThat(ret.getPostList().get(index).getUserImage()).isEqualTo(
                favoriteResponseList.get(index).getUserImage());
            assertThat(ret.getPostList().get(index).getTagList()).isEqualTo(
                favoriteResponseList.get(index).getTagList());
        }
    }

    @Test
    @DisplayName("Test delete all")
    public void deleteAllTest() {
        favoriteService.deleteAll(user, "project");
        assertThat(user.getRecruitFavorites().size()).isEqualTo(1);
    }
}
