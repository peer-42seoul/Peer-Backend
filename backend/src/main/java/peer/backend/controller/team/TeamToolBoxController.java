package peer.backend.controller.team;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.entity.user.User;
import peer.backend.service.team.TeamService;
import peer.backend.service.team.TeamToolsBoxService;

@Controller
@Secured("USER_ROLE")
@RestController
@RequiredArgsConstructor
@RequestMapping(TeamController.TEAM_URL)
public class TeamToolBoxController {
    public static final String TEAM_URL = "/api/v1/team";
    private final TeamService teamService;
    private final TeamToolsBoxService teamToolsBoxService;

    // TODO: 공지사항 게시판
    // TODO: 게시판 jwee님과 상의

    // TODO: 회원 주소록
    @GetMapping("/addressBook/{teamId}/{size}")
    public void getAddressBook(@PathVariable() Long teamId, @PathVariable() int size, Authentication authentication) {
        this.teamToolsBoxService.getAddressBook(teamId, size, User.authenticationToUser(authentication));
    }
    // TODO: 캘린더 미리보기 ??
    // TODO: 출석체크 ??
    // 몽고 db로 해결 가능
    // TODO: 텍스트 박스
    // TODO: 이미지 박스
}
