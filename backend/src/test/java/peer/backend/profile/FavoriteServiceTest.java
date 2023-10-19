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
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import peer.backend.dto.profile.FavoriteResponse;
import peer.backend.entity.board.recruit.Recruit;
import peer.backend.entity.board.recruit.RecruitFavorite;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.team.enums.TeamUserRoleType;
import peer.backend.entity.user.User;
import peer.backend.repository.user.UserRepository;
import peer.backend.service.profile.FavoriteService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
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
                    .id((long)index + 1)
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
                    .build();
            Recruit recruit = Recruit.builder()
                    .id((long)index)
                    .team(team)
                    .title("test title " + index)
                    .link("test link " + index)
                    .thumbnailUrl("test image " + index)
                    .build();
            RecruitFavorite recruitFavorite = new RecruitFavorite(
                    user.getId(), recruit.getId(), user, recruit, true
            );
            recruitFavoriteList.add(recruitFavorite);
        }
    }

    @Test
    @DisplayName("test get favorite")
    public void getFavoriteTest() {
        when(userRepository.findByName(anyString())).thenReturn(Optional.of(user));
        Page<FavoriteResponse> favoriteServicePage = favoriteService.getFavorite(name, "project", 0, 10);
        String json = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            json = objectMapper.writeValueAsString(favoriteServicePage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println(json);
    }
}
