package peer.backend.controller.team;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.team.BoardRes;
import peer.backend.dto.team.PostRes;
import peer.backend.dto.team.SimpleBoardRes;
import peer.backend.entity.board.team.Board;
import peer.backend.repository.board.team.BoardRepository;
import peer.backend.service.board.team.BoardService;
import peer.backend.service.teampage.TeamPageService;

@RestController
@RequiredArgsConstructor
@RequestMapping(TeamPageController.TEAM_URL)
public class TeamPageController {
    private final TeamPageService teamPageService;
    private final BoardService boardService;
    public static final String TEAM_URL = "/api/v1/team-page";

    @GetMapping("/posts/{boardId}")
    public ResponseEntity<BoardRes> getPosts(@PathVariable("boardId") Long boardId, Pageable pageable) {
        Page<PostRes> postsPage = teamPageService.getPostsByBoardId(boardId, pageable);

        if (!postsPage.isEmpty()) {
            Board board = boardService.getBoardById(boardId);
            BoardRes res = new BoardRes(board.getId(), board.getName(), postsPage.getContent());
            return ResponseEntity.ok(res);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/simple/{teamId}")
    public ResponseEntity<List<SimpleBoardRes>> getSimpleBoardList(@PathVariable("teamId") Long teamId,
                                                                   Authentication auth) {
        List<SimpleBoardRes> boards = boardService.getSimpleBoards(teamId, auth);
        return ResponseEntity.ok(boards);
    }
}
