package peer.backend.team;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.bytebuddy.dynamic.DynamicType.Builder.RecordComponentDefinition;
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
import peer.backend.entity.user.InterestedProject;
import peer.backend.entity.user.User;
import peer.backend.repository.team.TeamRepository;
import peer.backend.repository.team.TeamUserRepository;
import peer.backend.repository.user.InterestedProjectRepository;
import peer.backend.repository.user.UserRepository;
import peer.backend.service.team.TeamService;
import peer.backend.service.user.InterestedProjectService;

@ExtendWith(MockitoExtension.class)
@DisplayName("InterestedService Test")
public class InterestedServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private InterestedProjectRepository interestedProjectRepository;

    @InjectMocks
    private InterestedProjectService interestedProjectService;

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
    @DisplayName("getInterestedProjectList 함수 테스트")
    void addInterestedProjectTest() {
        InterestedProject interestedProject = InterestedProject.builder()
            .user(user)
            .team(team)
            .userId(user.getId())
            .teamId(team.getId())
            .build();
        List<InterestedProject> interestedProjectList = new ArrayList<>();
        interestedProjectList.add(interestedProject);
        user.setInterestedProjects(interestedProjectList);

        Optional<User> opUser = Optional.of(user);
        when(userRepository.findById(anyLong())).thenReturn(opUser);
        List<Team> find = interestedProjectService.getInterestedProjectList(user.getId());
        assertEquals(find.get(0).getName(), team.getName());
    }
}
