package peer.backend.board;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import peer.backend.entity.board.recruit.Recruit;
import peer.backend.entity.board.recruit.enums.RecruitStatus;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.enums.TeamMemberStatus;
import peer.backend.entity.team.enums.TeamOperationFormat;
import peer.backend.entity.team.enums.TeamStatus;
import peer.backend.entity.team.enums.TeamType;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.board.recruit.RecruitRepository;
import peer.backend.repository.team.TeamRepository;

@DisplayName("Recruit Repository 테스트")
@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest
public class RecruitRepositoryTest {

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    RecruitRepository recruitRepository;

    Team team;
    Recruit recruit;

    @BeforeEach
    void beforeEach() {
        team = Team.builder()
            .id(1L)
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
            .teamUsers(null)
            .build();
        recruit = Recruit.builder()
            .id(1L)
            .link("t")
            .due("t")
            .content("t")
            .place(TeamOperationFormat.ONLINE)
            .region("t")
            .status(RecruitStatus.BEFORE)
            .team(team)
            .thumbnailUrl("t")
            .title("t")
            .type(TeamType.PROJECT)
            .build();
    }

//    @Test
//    @DisplayName("recruit save test")
//    void findTest() {
//        assertEquals(recruitRepository.count(), 0);
//        teamRepository.save(team);
//        recruitRepository.save(recruit);
//        Recruit recruit1 = recruitRepository.findById(1L).orElseThrow(()-> new NotFoundException("hoho"));
//        assertThat("t").isEqualTo(recruit1.getLink());
//    }
}
