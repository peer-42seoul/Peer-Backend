package peer.backend.team;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.AntPathMatcher;
import peer.backend.controller.team.TeamController;
import peer.backend.dto.team.TeamListResponse;
import peer.backend.entity.board.recruit.enums.RecruitDueEnum;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.team.enums.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import peer.backend.service.team.TeamService;

@SpringBootTest
@AutoConfigureMockMvc
public class TeamControllerTest {

    MockMvc mockMvc;

    @Mock
    TeamService teamService;

    @InjectMocks
    TeamController teamController;

    @Autowired
    ObjectMapper objectMapper;

    Team team;
    TeamUser teamUser;

    @BeforeEach
    void beforeEach() {
        mockMvc = standaloneSetup(teamController).build();
        team = Team.builder()
            .name("test")
            .type(TeamType.STUDY)
            .dueTo(RecruitDueEnum.EIGHT_MONTHS)
            .operationFormat(TeamOperationFormat.ONLINE)
            .status(TeamStatus.ONGOING)
            .teamMemberStatus(TeamMemberStatus.RECRUITING)
            .isLock(false)
            .region1("test")
            .region2("test")
            .build();
//
//        teamUser = TeamUser.builder()
//            .team(team)
//            .user(user)
//            .teamUserRoleType(TeamUserRoleType.LEADER)
//            .build();
    }

//    @Test
//    @DisplayName("getTeamList Test")
//    void getTeamListTest() throws Exception {
//        List<TeamListResponse> teamList = new ArrayList<>();
//        TeamListResponse teamListResponse = new TeamListResponse(team, teamU);
//        teamList.add(teamListResponse);
//
//        when(teamService.getTeamList(anyLong(), eq(-1))).thenReturn(teamList);
//
//        MvcResult mvcResult = mockMvc.perform(get(TeamController.TEAM_URL + "/" + 0)
//            .param("teamStatus", "-1"))
//            .andDo(print())
//            .andReturn();
//        String json = mvcResult.getResponse().getContentAsString();
//        List<TeamListResponse> list = objectMapper.readValue(json, new TypeReference<List<TeamListResponse>>() {});
//        System.out.println(list.get(0).getName());
//        assertEquals(list.get(0).getName(), team.getName());
//    }

}
