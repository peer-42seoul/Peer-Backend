package peer.backend.board;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import peer.backend.dto.board.recruit.RecruitListRequestDTO;
import peer.backend.entity.board.recruit.Recruit;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.enums.TeamMemberStatus;
import peer.backend.entity.team.enums.TeamOperationFormat;
import peer.backend.entity.team.enums.TeamStatus;
import peer.backend.entity.team.enums.TeamType;
import peer.backend.entity.user.User;
import peer.backend.repository.board.recruit.RecruitApplicantRepository;
import peer.backend.repository.board.recruit.RecruitFavoriteRepository;
import peer.backend.repository.board.recruit.RecruitRepository;
import peer.backend.repository.team.TeamRepository;
import peer.backend.repository.user.UserRepository;
import peer.backend.service.board.recruit.RecruitService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RecruitService Test")
public class RecruitServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    RecruitRepository recruitRepository;
    @Mock
    TeamRepository teamRepository;
    @Mock
    RecruitFavoriteRepository recruitFavoriteRepository;
    @Mock
    RecruitApplicantRepository recruitApplicantRepository;
    @InjectMocks
    RecruitService recruitService;

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
    void createRecruitTest() throws IOException {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        RecruitListRequestDTO recruitListRequestDTO = RecruitListRequestDTO.builder()
                .name("hello")
                .due("1주일")
                .region("고양시")
                .content("abcd")
                .status("모집중")
                .title("hi")
                .tagList(new ArrayList<>())
                .place("온라인")
                .link("www.naver.com")
                .userId(1L)
                .type("스터디")
                .build();
        recruitService.createRecruit(recruitListRequestDTO);
        Recruit recruit = recruitRepository.findById(1L).get();
        assertThat("hi").isEqualTo(recruit.getTitle());
    }
}
