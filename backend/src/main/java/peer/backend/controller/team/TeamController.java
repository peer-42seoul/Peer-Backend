package peer.backend.controller.team;

import io.swagger.annotations.ApiOperation;

import java.io.IOException;
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
    @GetMapping("/list")
    public List<TeamListResponse> getTeamList(@RequestParam("teamStatus") String teamStatus, Authentication authentication) {
        TeamStatus teamStatus1 = TeamStatus.valueOf(teamStatus.toUpperCase());
        User thisUser = User.authenticationToUser(authentication);
        return this.teamService.getTeamList(teamStatus1, thisUser);
    }

    @GetMapping("/setting/{teamId}")
    public TeamSettingDto getTeamSetting(@PathVariable() Long teamId, Authentication authentication) {
        User thisUser = User.authenticationToUser(authentication);
        return this.teamService.getTeamSetting(teamId, thisUser);
    }

    @PostMapping("/setting/{teamId}")
    public ResponseEntity<?> updateTeamSetting(@PathVariable() Long teamId, @RequestBody @Valid TeamSettingInfoDto teamSettingInfoDto, Authentication authentication) throws IOException {
        this.teamService.updateTeamSetting(teamId, teamSettingInfoDto, User.authenticationToUser(authentication));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete/{teamId}")
    public ArrayList<TeamMemberDto> deleteTeamMember(@PathVariable() Long teamId, @RequestParam("userId") Long userId, Authentication authentication) {
        System.out.println("deleteTeamMember");
        return this.teamService.deleteTeamMember(teamId, userId, User.authenticationToUser(authentication));
    }

    @PostMapping("/grant/{teamId}")
    public ResponseEntity<?> grantRole(@PathVariable() Long teamId, @RequestParam("userId") Long userId, @RequestParam("role") String teamUserRoleType, Authentication authentication) {
        try {
            TeamUserRoleType teamUserRoleType1 = TeamUserRoleType.valueOf(teamUserRoleType.toUpperCase());
            User user = User.authenticationToUser(authentication);
            this.teamService.grantRole(teamId, userId, user, teamUserRoleType1);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("잘못된 권한입니다.");
        }
    }

    @DeleteMapping("/exit")
    public ResponseEntity<?> exitTeam(@RequestParam("teamId") Long teamId, Authentication authentication) {
        User user = User.authenticationToUser(authentication);
        this.teamService.exitTeam(teamId, user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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

    @GetMapping("/main/member/{teamId}")
    public List<TeamMemberDto> getTeamMember(@PathVariable() Long teamId, Authentication authentication) {
        User user = User.authenticationToUser(authentication);
        return this.teamService.getTeamMemberList(teamId, user);
    }

    @DeleteMapping("/setting/image/{teamId}")
    public ResponseEntity<?> deleteTeamImage(@PathVariable() Long teamId, Authentication authentication) throws IOException {
        User user = User.authenticationToUser(authentication);
        this.teamService.deleteTeamImage(teamId, user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/setting/image/{teamId}")
    public ResponseEntity<?> updateTeamImage(@PathVariable() Long teamId, @ModelAttribute TeamImageDto teamImageDto, Authentication authentication) throws IOException {
        User user = User.authenticationToUser(authentication);
        this.teamService.updateTeamImage(teamId, teamImageDto, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
