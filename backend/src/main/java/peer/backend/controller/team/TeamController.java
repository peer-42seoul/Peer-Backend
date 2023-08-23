package peer.backend.controller.team;

import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.team.TeamListResponseDto;
import peer.backend.dto.team.TeamResponseDto;
import peer.backend.entity.team.Team;
import peer.backend.service.team.TeamService;

@RestController
@RequiredArgsConstructor
@RequestMapping(TeamController.TEAM_URL)
public class TeamController {

    public static final String TEAM_URL = "/api/v1/team";

    private final TeamService teamService;

    @ApiOperation(value = "C-MYPAGE-49", notes = "속한 팀 리스트를 가져옵니다.")
    @GetMapping("/{userId}")
    public List<TeamListResponseDto> getTeamList(@PathVariable() Long userId) {
        List<Team> teamList = this.teamService.getTeamList(userId);
        List<TeamListResponseDto> teamListResponseDtoList = teamList.stream()
            .map(x -> new TeamListResponseDto(x)).collect(
                Collectors.toList());

        return teamListResponseDtoList;
    }

    @ApiOperation(value = "C-MYPAGE-49", notes = "팀 아이디로 세부 정보를 가져옵니다.")
    @GetMapping("/id/{teamId}")
    public TeamResponseDto getTeamById(@PathVariable() Long teamId) {
        Team team = this.teamService.getTeamById(teamId);
        TeamResponseDto teamResponseDto = new TeamResponseDto(team);

        return teamResponseDto;
    }

    @ApiOperation(value = "C-MYPAGE-49", notes = "팀 이름으로 세부 정보를 가져옵니다.")
    @GetMapping("/name/{teamName}")
    public TeamResponseDto getTeamById(@PathVariable() String teamName) {
        Team team = this.teamService.getTeamByName(teamName);
        TeamResponseDto teamResponseDto = new TeamResponseDto(team);

        return teamResponseDto;
    }
}
