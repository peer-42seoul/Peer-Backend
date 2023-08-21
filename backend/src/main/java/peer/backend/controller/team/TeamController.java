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

}
