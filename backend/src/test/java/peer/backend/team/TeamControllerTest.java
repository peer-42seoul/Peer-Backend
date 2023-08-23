package peer.backend.team;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import peer.backend.controller.team.TeamController;
import peer.backend.dto.team.TeamListResponse;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.enums.TeamMemberStatus;
import peer.backend.entity.team.enums.TeamOperationFormat;
import peer.backend.entity.team.enums.TeamStatus;
import peer.backend.entity.team.enums.TeamType;
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

    @BeforeEach
    void beforeEach() {
        mockMvc = standaloneSetup(teamController).build();
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
    @DisplayName("getTeamList Test")
    void getTeamListTest() throws Exception {
        List<Team> teamList = new ArrayList<>();
        teamList.add(team);

        when(teamService.getTeamList(anyLong())).thenReturn(teamList);

        MvcResult mvcResult = mockMvc.perform(get(TeamController.TEAM_URL + "/" + anyLong()))
            .andDo(print())
            .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        List<TeamListResponse> list = objectMapper.readValue(json,
            new TypeReference<List<TeamListResponse>>() {
            });

        assertEquals(list.get(0).getName(), team.getName());
    }

}
