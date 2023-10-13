package peer.backend.controller.team;

import io.swagger.annotations.ApiOperation;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import peer.backend.dto.team.*;
import peer.backend.entity.team.enums.TeamStatus;
import peer.backend.entity.team.enums.TeamUserRoleType;
import peer.backend.service.team.TeamService;

import javax.validation.Valid;

@Secured("USER_ROLE")
@RestController
@RequiredArgsConstructor
@RequestMapping(TeamController.TEAM_URL)
public class TeamController {

    public static final String TEAM_URL = "/api/v1/team";
    private final TeamService teamService;

    @ApiOperation(value = "C-MYPAGE-49 ~ 53", notes = "유저가 속한 팀 리스트를 가져옵니다.")
    @GetMapping("/list/{userId}")
    public List<TeamListResponse> getTeamList(@PathVariable() Long userId, @RequestParam("teamStatus") TeamStatus teamStatus) {
        //TODO: Principal 유저 아이디 가져와서 같은지 확인
        return this.teamService.getTeamList(userId, teamStatus);
    }

    @GetMapping("/setting/{teamId}")
    public TeamSettingDto getTeamSetting(@PathVariable() Long teamId, Principal principal) {
        principal.getName(); // Email
        return this.teamService.getTeamSetting(teamId, principal.getName());
    }

    @PostMapping("/setting/{teamId}")
    public ResponseEntity<?> updateTeamSetting(@PathVariable() Long teamId, @RequestBody @Valid TeamSettingInfoDto team, Principal principal) {
        this.teamService.updateTeamSetting(teamId, team, principal.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete/{teamId}")
    public ArrayList<TeamMemberDto> deleteTeamMember(@PathVariable() Long teamId, @RequestParam("userId") String userId, Principal principal) {
        System.out.println("deleteTeamMember");
        return this.teamService.deleteTeamMember(teamId, userId, principal.getName());
    }

    @PostMapping("/grant/{teamId}")
    public ResponseEntity<?> grantRole(@PathVariable() Long teamId, @RequestParam("userId") Long userId, @RequestParam("role") TeamUserRoleType teamUserRoleType, Principal principal) {
        this.teamService.grantRole(teamId, userId, principal.getName(), teamUserRoleType);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/exit")
    public ResponseEntity<?> exitTeam(@RequestParam("teamId") Long teamId, Principal principal) {
        this.teamService.exitTeam(teamId, principal.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

/*
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
    public void updateTeam(@PathVariable() Long teamId, @Valid() @RequestBody() UpdateTeamRequest updateTeamRequest) {
        this.teamService.updateTeam(teamId, updateTeamRequest);
    }

//         TODO: 권한 검증 추가
    @ApiOperation(value = "I-TM-10", notes = "팀에서 유저를 추방 시킵니다.")
    @DeleteMapping("/kick")
    public void kickMember(@RequestBody() TeamMemberKickRequest request) {
        this.teamService.deleteTeamUser(request.getTeamId(), request.getUserId());
    }
*/

//    @GetMapping("/info/{teamId}")
//    public TeamInfoResponse getTeamInfo(Authentication authentication, @PathVariable() Long teamId) {
//        return this.teamService.getTeamInfo(teamId, authentication.getName());
//    }
}
