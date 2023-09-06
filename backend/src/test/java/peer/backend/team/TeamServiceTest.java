package peer.backend.team;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.team.enums.TeamMemberStatus;
import peer.backend.entity.team.enums.TeamOperationFormat;
import peer.backend.entity.team.enums.TeamStatus;
import peer.backend.entity.team.enums.TeamType;
import peer.backend.entity.user.User;
import peer.backend.repository.team.TeamRepository;
import peer.backend.repository.team.TeamUserRepository;
import peer.backend.service.team.TeamService;

@ExtendWith(MockitoExtension.class)
@DisplayName("TeamService Test")
public class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamUserRepository teamUserRepository;

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
            .name("test")
            .type(TeamType.STUDY)
            .dueTo("10월")
            .operationFormat(TeamOperationFormat.ONLINE)
            .status(TeamStatus.RECRUITING)
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
            .build();
        List<TeamUser> teamUserList = new ArrayList<>();
        teamUserList.add(teamUser);

        when(teamUserRepository.findByUserId(anyLong())).thenReturn(teamUserList);
        assertEquals(teamService.getTeamList(0L).get(0).getName(), team.getName());
    }
}
