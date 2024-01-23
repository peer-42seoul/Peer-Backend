package peer.backend.controller.team;

import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.team.TeamApplicantListDto;
import peer.backend.dto.team.TeamIdRequest;
import peer.backend.dto.team.TeamInfoResponse;
import peer.backend.dto.team.TeamJobCreateRequest;
import peer.backend.dto.team.TeamJobUpdateDto;
import peer.backend.dto.team.TeamListResponse;
import peer.backend.dto.team.TeamMemberDto;
import peer.backend.dto.team.TeamSettingDto;
import peer.backend.dto.team.TeamSettingInfoDto;
import peer.backend.entity.composite.TeamUserJobPK;
import peer.backend.entity.team.enums.TeamStatus;
import peer.backend.entity.team.enums.TeamUserRoleType;
import peer.backend.entity.user.User;
import peer.backend.exception.BadRequestException;
import peer.backend.service.profile.UserPortfolioService;
import peer.backend.service.team.TeamService;

@Secured("USER_ROLE")
@RestController
@RequiredArgsConstructor
@RequestMapping(TeamController.TEAM_URL)
public class TeamController {

    public static final String TEAM_URL = "/api/v1/team";
    private final TeamService teamService;
    private final UserPortfolioService userPortfolioService;

    @ApiOperation(value = "C-MYPAGE-49 ~ 53", notes = "GET-유저가 속한 팀 리스트를 가져옵니다.")
    @GetMapping("/list")
    public List<TeamListResponse> getTeamList(@RequestParam("teamStatus") String teamStatus,
        Authentication authentication) {
        TeamStatus teamStatus1 = TeamStatus.valueOf(teamStatus.toUpperCase());
        User thisUser = User.authenticationToUser(authentication);
        return this.teamService.getTeamList(teamStatus1, thisUser);
    }

    @ApiOperation(value = "TEAM-SETTING", notes = "GET-팀 설정 정보를 가져옵니다.")
    @GetMapping("/setting/{teamId}")
    public TeamSettingDto getTeamSetting(@PathVariable() Long teamId,
        Authentication authentication) {
        User thisUser = User.authenticationToUser(authentication);
        return this.teamService.getTeamSetting(teamId, thisUser);
    }

    @ApiOperation(value = "TEAM-LIST", notes = "POST-팀 정보를 설정합니다.")
    @PostMapping("/setting/{teamId}")
    public ResponseEntity<?> updateTeamSetting(@PathVariable() Long teamId,
        @RequestBody @Valid TeamSettingInfoDto teamSettingInfoDto, Authentication authentication) {
        this.teamService.updateTeamSetting(teamId, teamSettingInfoDto,
            User.authenticationToUser(authentication));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "TEAM-LIST", notes = "DELETE-팀원을 삭제합니다.")
    @DeleteMapping("/delete/{teamId}")
    public ArrayList<TeamMemberDto> deleteTeamMember(@PathVariable() Long teamId,
        @RequestParam("userId") Long userId, Authentication authentication) {
        System.out.println("deleteTeamMember");
        return this.teamService.deleteTeamMember(teamId, userId,
            User.authenticationToUser(authentication));
    }

    @ApiOperation(value = "TEAM-LIST", notes = "POST-팀원의 역할(리더, 멤버)을 변경합니다.")
    @PostMapping("/grant/{teamId}")
    public ResponseEntity<?> grantRole(@PathVariable() Long teamId,
        @RequestParam("userId") Long userId, @RequestParam("role") String teamUserRoleType,
        Authentication authentication) {
        try {
            TeamUserRoleType teamUserRoleType1 = TeamUserRoleType.valueOf(
                teamUserRoleType.toUpperCase());
            User user = User.authenticationToUser(authentication);
            this.teamService.grantRole(teamId, userId, user, teamUserRoleType1);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("잘못된 권한입니다.");
        }
    }

    @ApiOperation(value = "TEAM-LIST", notes = "DELETE-팀을 나갑니다.")
    @DeleteMapping("/exit")
    public ResponseEntity<?> exitTeam(@RequestParam("teamId") Long teamId,
        Authentication authentication) {
        User user = User.authenticationToUser(authentication);
        this.teamService.exitTeam(teamId, user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/applicant/{teamId}")
    public List<TeamApplicantListDto> getTeamApplicant(@PathVariable() Long teamId,
        Authentication authentication) {
        return this.teamService.getTeamApplicantList(teamId,
            User.authenticationToUser(authentication));
    }

    @PutMapping("/applicant/accept/{teamId}")
    public List<TeamApplicantListDto> acceptTeamApplicant(@PathVariable() Long teamId,
        @RequestBody TeamUserJobPK applicantId, Authentication authentication) {
        User thisUser = User.authenticationToUser(authentication);
        this.teamService.acceptTeamApplicant(teamId, applicantId, thisUser);
        return this.teamService.getTeamApplicantList(teamId, thisUser);
    }

    @PutMapping("/applicant/reject/{teamId}")
    public List<TeamApplicantListDto> rejectTeamApplicant(@PathVariable() Long teamId,
        @RequestBody TeamUserJobPK applicantId, Authentication authentication) {
        User thisUser = User.authenticationToUser(authentication);
        this.teamService.rejectTeamApplicant(teamId, applicantId, thisUser);
        return this.teamService.getTeamApplicantList(teamId, thisUser);
    }

    @GetMapping("/main/{teamId}")
    public TeamInfoResponse getTeamInfo(@PathVariable() Long teamId,
        Authentication authentication) {
        User user = User.authenticationToUser(authentication);
        return this.teamService.getTeamInfo(teamId, user);
    }

    @GetMapping("/main/member/{teamId}")
    public List<TeamMemberDto> getTeamMember(@PathVariable() Long teamId,
        Authentication authentication) {
        User user = User.authenticationToUser(authentication);
        return this.teamService.getTeamMemberList(teamId, user);
    }

    @PutMapping("/setting/job/change")
    public ResponseEntity<Object> updateTeamJobSetting(@RequestBody @Valid TeamJobUpdateDto job,
        Authentication auth) {
        User user = User.authenticationToUser(auth);
        return teamService.updateTeamJob(job, user);
    }

    @PostMapping("/setting/job/add/{teamId}")
    public ResponseEntity<Object> createTeamJob(@PathVariable Long teamId,
        @RequestBody @Valid TeamJobCreateRequest job, Authentication auth) {
        User user = User.authenticationToUser(auth);
        return teamService.createTeamJob(teamId, job, user);
    }

    @DeleteMapping("/setting/job/delete/{jobId}")
    public ResponseEntity<Object> deleteTeamJob(@PathVariable Long jobId, Authentication auth) {
        User user = User.authenticationToUser(auth);
        return teamService.deleteTeamJob(jobId, user);
    }

    @PostMapping("/setting/quit")
    public ResponseEntity<Void> quitTeam(@RequestBody @Valid TeamIdRequest request,
        Authentication auth) {
        User user = User.authenticationToUser(auth);
        this.teamService.quitTeam(user, request.getTeamId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/setting/disperse")
    public ResponseEntity<Void> disperseTeam(@RequestBody @Valid TeamIdRequest request,
        Authentication auth) {
        User user = User.authenticationToUser(auth);
        this.teamService.disperseTeam(user, request.getTeamId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/setting/finish")
    public ResponseEntity<Void> finishTeam(@RequestBody @Valid TeamIdRequest request,
        Authentication auth) {
        User user = User.authenticationToUser(auth);
        this.teamService.finishTeam(user, request.getTeamId());
        try {
            this.userPortfolioService.makeWholeTeamUserForPortfolio(request.getTeamId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }
}
