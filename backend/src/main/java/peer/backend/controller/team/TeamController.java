package peer.backend.controller.team;

import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.team.TeamListResponse;
import peer.backend.dto.team.TeamMemberKickRequest;
import peer.backend.dto.team.TeamResponse;
import peer.backend.dto.team.UpdateTeamRequest;
import peer.backend.entity.team.Team;
import peer.backend.exception.NotFoundException;
import peer.backend.service.team.TeamService;

@RestController
@RequiredArgsConstructor
@RequestMapping(TeamController.TEAM_URL)
public class TeamController {

    public static final String TEAM_URL = "/api/v1/team";

    private final TeamService teamService;

    @ApiOperation(value = "C-MYPAGE-49", notes = "속한 팀 리스트를 가져옵니다.")
    @GetMapping("/{userId}")
    public List<TeamListResponse> getTeamList(@PathVariable() Long userId) {
        List<Team> teamList = this.teamService.getTeamList(userId);

        return teamList.stream()
            .map(TeamListResponse::new).collect(
                Collectors.toList());
    }

    @ApiOperation(value = "C-MYPAGE-49", notes = "팀 아이디로 세부 정보를 가져옵니다.")
    @GetMapping("/id/{teamId}")
    public TeamResponse getTeamById(@PathVariable() Long teamId) {
        Team team = this.teamService.getTeamById(teamId);

        return new TeamResponse(team);
    }

    @ApiOperation(value = "C-MYPAGE-49", notes = "팀 이름으로 세부 정보를 가져옵니다.")
    @GetMapping("/name/{teamName}")
    public TeamResponse getTeamById(@PathVariable() String teamName) {
        Team team = this.teamService.getTeamByName(teamName);

        return new TeamResponse(team);
    }

    @ApiOperation(value = "I-TM-01 ~ I-TM-13", notes = "팀 정보를 업데이트 합니다.")
    @PutMapping("/{teamId}")
    public void updateTeam(@PathVariable() Long teamId,
        @Valid() @RequestBody() UpdateTeamRequest updateTeamRequest) {
        this.teamService.updateTeam(teamId, updateTeamRequest);
    }

    // TODO: 권한 검증 추가
    @ApiOperation(value = "I-TM-10", notes = "팀에서 유저를 추방 시킵니다.")
    @DeleteMapping("/kick")
    public void kickMember(@RequestBody() TeamMemberKickRequest request) {
        this.teamService.deleteTeamUser(request.getTeamId(), request.getUserId());
    }
}
