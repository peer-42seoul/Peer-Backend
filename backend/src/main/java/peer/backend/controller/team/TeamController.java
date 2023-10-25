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
import peer.backend.entity.user.User;
import peer.backend.exception.BadRequestException;
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
    public ResponseEntity<?> grantRole(@PathVariable() Long teamId, @RequestParam("userId") Long userId, @RequestParam("role") String teamUserRoleType, Principal principal) {
        try {
            TeamUserRoleType teamUserRoleType1 = TeamUserRoleType.valueOf(teamUserRoleType.toUpperCase());
            this.teamService.grantRole(teamId, userId, principal.getName(), teamUserRoleType1);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("잘못된 권한입니다.");
        }
    }

    @DeleteMapping("/exit")
    public ResponseEntity<?> exitTeam(@RequestParam("teamId") Long teamId, Principal principal) {
        this.teamService.exitTeam(teamId, principal.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/applicant/{teamId}")
    public List<TeamApplicantListDto> getTeamApplicant(@PathVariable() Long teamId, Authentication authentication) {
        return this.teamService.getTeamApplicantList(teamId, User.authenticationToUser(authentication));
    }

    @PutMapping("/applicant/accept/{teamId}")
    public List<TeamApplicantListDto> acceptTeamApplicant(@PathVariable() Long teamId, @RequestParam("userId") Long applicantId, Authentication authentication) {
        User thisUser =  User.authenticationToUser(authentication);
        this.teamService.acceptTeamApplicant(teamId, applicantId, thisUser);
        return this.teamService.getTeamApplicantList(teamId, thisUser);
    }

    @PutMapping("/applicant/reject/{teamId}")
    public List<TeamApplicantListDto> rejectTeamApplicant(@PathVariable() Long teamId, @RequestParam("userId") Long applicantId, Authentication authentication) {
        User thisUser =  User.authenticationToUser(authentication);
        this.teamService.rejectTeamApplicant(teamId, applicantId, thisUser);
        return this.teamService.getTeamApplicantList(teamId, thisUser);
    }

    @GetMapping("/main/{teamId}")
    public TeamInfoResponse getTeamInfo(@PathVariable() Long teamId, Authentication authentication) {
        User user = User.authenticationToUser(authentication);
        return this.teamService.getTeamInfo(teamId, user);
    }
}
