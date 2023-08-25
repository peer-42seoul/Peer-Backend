package peer.backend.team;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
import peer.backend.repository.team.TeamRepository;

@DisplayName("Team Repository 테스트")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TeamRepositoryTest {

    @Autowired
    TeamRepository teamRepository;

    Team team;

    @BeforeEach
    void beforeEach() {
        team = Team.builder()
            .name("unit_test")
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
    @DisplayName("Team Repository insert test")
    void insertTest() {
        assertEquals(teamRepository.count(), 0);
        teamRepository.save(team);
        assertEquals(teamRepository.count(), 1);
    }

    @Test
    @DisplayName("Team Repository select test")
    void selectTest() {
        teamRepository.save(team);
        assertEquals(teamRepository.findAll().size(), 1);
    }

    @Test
    @DisplayName("Team Repository delete test")
    void deleteTest() {
        teamRepository.save(team);
        teamRepository.deleteAll();
        assertEquals(teamRepository.count(), 0);
    }

    @Test
    @DisplayName("Team Repository findByName test")
    void findByNameTest() {
        teamRepository.save(team);
        Team find = teamRepository.findByName(team.getName()).orElse(null);
        assertThat(find).isNotNull();
        assertEquals(find.getName(), team.getName());
    }
}
