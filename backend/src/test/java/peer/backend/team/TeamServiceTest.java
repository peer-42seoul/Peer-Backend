package peer.backend.team;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import peer.backend.dto.team.TeamListResponse;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.team.enums.*;
import peer.backend.entity.user.User;
import peer.backend.repository.team.TeamUserRepository;
import peer.backend.repository.user.UserRepository;
import peer.backend.service.team.TeamService;

@ExtendWith(MockitoExtension.class)
@DisplayName("TeamService Test")
public class TeamServiceTest {

    @Mock
    private TeamUserRepository teamUserRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TeamService teamService;

    User user;

    Team team;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
            .id(1L)
            .name("test")
            .email("test@test.com")
            .nickname("test")
            .birthday(LocalDate.now())
            .isAlarm(false)
            .phone("test")
            .address("test")
            .certification(false)
            .company("test")
            .introduce("test")
            .peerLevel(0L)
            .representAchievement("test")
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
            .region1("test")
            .region2("test")
            .region3("test")
            .build();
    }

    @Test
    @DisplayName("getTeamList 함수 테스트")
    void getTeamListTest() {
        TeamUser teamUser = TeamUser.builder()
            .user(user)
            .team(team)
            .userId(user.getId())
            .teamId(team.getId())
            .role(TeamUserRoleType.LEADER)
            .build();
        List<TeamUser> teamUserList = new ArrayList<>();
        teamUserList.add(teamUser);

        user.setTeamUsers(teamUserList);
        TeamListResponse teamListResponse = new TeamListResponse(team, teamUser.getRole());

        Optional<User> opUser = Optional.of(user);

        when(userRepository.findById(anyLong())).thenReturn(opUser);
        when(teamUserRepository.findByUserIdAndTeamId(anyLong(), anyLong())).thenReturn(teamUser);

        assertEquals(teamService.getTeamList(anyLong(), -1).get(0).getName(), teamListResponse.getName());

    }
}
