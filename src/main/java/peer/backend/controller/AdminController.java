package peer.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.KeywordRequest;
import peer.backend.dto.team.TeamDefaultResponse;
import peer.backend.dto.user.UserDefaultResponse;
import peer.backend.entity.team.Team;
import peer.backend.entity.user.User;
import peer.backend.service.UserService;
import peer.backend.service.team.TeamService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final UserService userService;
    private final TeamService teamService;

    @GetMapping("user")
    public ResponseEntity<Page<UserDefaultResponse>> getUserDefaultList(Pageable pageable) {
        Page<User> userList = this.userService.getUserListFromPageable(pageable);
        return ResponseEntity.ok().body(userList.map(UserDefaultResponse::new));
    }

    @GetMapping("user/nickname")
    public ResponseEntity<Page<UserDefaultResponse>> searchUserDefaultListByNickname(
        Pageable pageable,
        @RequestBody KeywordRequest request) {
        Page<User> userList = this.userService.searchUserListByNicknameFromPageable(pageable,
            request.getKeyword());
        return ResponseEntity.ok().body(userList.map(UserDefaultResponse::new));
    }

    @GetMapping("team")
    public ResponseEntity<Page<TeamDefaultResponse>> getTeamDefaultList(Pageable pageable) {
        Page<Team> teamList = this.teamService.getTeamListFromPageable(pageable);
        return ResponseEntity.ok().body(teamList.map(TeamDefaultResponse::new));
    }
}
