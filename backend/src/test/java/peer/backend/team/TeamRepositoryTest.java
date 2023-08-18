package peer.backend.team;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
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
import peer.backend.repository.team.TeamRepository;

@DisplayName("Team Repository 테스트")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TeamRepositoryTest {

    @Autowired
    TeamRepository teamRepository;

    @Test
    @DisplayName("Team Repository insert test")
    void insertTest() {
        Team team = Team.builder()
            .name("test")
            .type(TeamType.STUDY)
            .dueTo("10월")
            .operationFormat(TeamOperationFormat.ONLINE)
            .status(TeamStatus.RECRUITING)
            .teamMemberStatus(TeamMemberStatus.RECRUITING)
            .region1("test")
            .region2("test")
            .build();

        assertEquals(teamRepository.count(), 0);
        teamRepository.save(team);
        assertEquals(teamRepository.count(), 1);
    }

    @Test
    @DisplayName("Team Repository select test")
    void selectTest() {
        Team team = Team.builder()
            .name("test")
            .type(TeamType.STUDY)
            .dueTo("10월")
            .operationFormat(TeamOperationFormat.ONLINE)
            .status(TeamStatus.RECRUITING)
            .teamMemberStatus(TeamMemberStatus.RECRUITING)
            .region1("test")
            .region2("test")
            .build();

        teamRepository.save(team);
        assertEquals(teamRepository.findAll().size(), 1);
    }

    @Test
    @DisplayName("Team Repository delete test")
    void deleteTest() {
        Team team = Team.builder()
            .name("test")
            .type(TeamType.STUDY)
            .dueTo("10월")
            .operationFormat(TeamOperationFormat.ONLINE)
            .status(TeamStatus.RECRUITING)
            .teamMemberStatus(TeamMemberStatus.RECRUITING)
            .region1("test")
            .region2("test")
            .build();

        teamRepository.save(team);
        teamRepository.deleteAll();
        assertEquals(teamRepository.count(), 0);
    }
}
