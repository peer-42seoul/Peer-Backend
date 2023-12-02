package peer.backend.board;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("RecruitService Test")
public class RecruitServiceTest {
//
//    @Mock
//    UserRepository userRepository;
//    @Mock
//    RecruitRepository recruitRepository;
//    @Mock
//    TeamRepository teamRepository;
//    @Mock
//    RecruitFavoriteRepository recruitFavoriteRepository;
//    @Mock
//    RecruitApplicantRepository recruitApplicantRepository;
//    @InjectMocks
//    RecruitService recruitService;
//
//    User user;
//
//    Team team;
//
//    @BeforeEach
//    void beforeEach() {
//        user = User.builder()
//            .id(1L)
//            .name("test")
//            .email("test@test.com")
//            .nickname("test")
//            .isAlarm(false)
//            .address("test")
//            .certification(false)
//            .company("test")
//            .introduce("test")
//            .peerLevel(0L)
//            .representAchievement("test")
//            .build();
//
//        team = Team.builder()
//            .name("test")
//            .type(TeamType.STUDY)
//            .dueTo("10월")
//            .operationFormat(TeamOperationFormat.ONLINE)
//            .status(TeamStatus.RECRUITING)
//            .teamMemberStatus(TeamMemberStatus.RECRUITING)
//            .isLock(false)
//            .region1("test")
//            .region2("test")
//            .region3("test")
//            .build();
//    }
//
//    @Test
//    @DisplayName("getInterestedProjectList 함수 테스트")
//    void createRecruitTest() throws IOException {
//        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
//        RecruitListRequestDTO recruitListRequestDTO = RecruitListRequestDTO.builder()
//                .name("hello")
//                .due("1주일")
//                .region("고양시")
//                .content("abcd")
//                .status("모집중")
//                .title("hi")
//                .tagList(new ArrayList<>())
//                .place("온라인")
//                .link("www.naver.com")
//                .userId(1L)
//                .type("스터디")
//                .build();
//        recruitService.createRecruit(recruitListRequestDTO);
//        Recruit recruit = recruitRepository.findById(1L).get();
//        assertThat("hi").isEqualTo(recruit.getTitle());
//    }
}
