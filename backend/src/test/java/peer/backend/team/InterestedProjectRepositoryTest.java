package peer.backend.team;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.enums.TeamMemberStatus;
import peer.backend.entity.team.enums.TeamOperationFormat;
import peer.backend.entity.team.enums.TeamStatus;
import peer.backend.entity.team.enums.TeamType;
import peer.backend.entity.user.InterestedProject;
import peer.backend.entity.user.User;
import peer.backend.repository.team.TeamRepository;
import peer.backend.repository.user.InterestedProjectRepository;
import peer.backend.repository.user.UserRepository;

@DisplayName("Interested Project Repository 테스트")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class InterestedProjectRepositoryTest {

    @Autowired
    InterestedProjectRepository interestedProjectRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TeamRepository teamRepository;

    InterestedProject interestedProject;

    @BeforeEach
    void beforeEach() {
        User user = User.builder()
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
        userRepository.save(user);

        Team team = Team.builder()
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
        teamRepository.save(team);
    }

    @Test
    @DisplayName("Interested Project insert test")
    void insertTest() {
        User user = userRepository.findAll().get(0);
        Team team = teamRepository.findAll().get(0);

        InterestedProject interestedProject = InterestedProject.builder()
            .user(user)
            .userId(user.getId())
            .team(team)
            .teamId(team.getId())
            .build();

        interestedProjectRepository.save(interestedProject);
        assertEquals(interestedProjectRepository.count(), 1);
    }

    @Test
    @DisplayName("Interested Project delete test")
    void deleteTest() {
        User user = userRepository.findAll().get(0);
        Team team = teamRepository.findAll().get(0);

        InterestedProject interestedProject = InterestedProject.builder()
            .user(user)
            .userId(user.getId())
            .team(team)
            .teamId(team.getId())
            .build();

        interestedProjectRepository.save(interestedProject);
        assertEquals(interestedProjectRepository.count(), 1);
        interestedProjectRepository.deleteByUserIdAndTeamId(user.getId(), team.getId());
        assertEquals(interestedProjectRepository.count(), 0);
    }
}
