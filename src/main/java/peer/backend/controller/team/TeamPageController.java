package peer.backend.controller.team;

import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.team.BoardRes;
import peer.backend.dto.team.PostRes;
import peer.backend.entity.board.team.Board;
import peer.backend.repository.board.team.BoardRepository;
import peer.backend.service.teampage.TeamPageService;

@RestController
@RequiredArgsConstructor
@RequestMapping(TeamPageController.TEAM_URL)
public class TeamPageController {
    private final TeamPageService teamPageService;
    private final BoardRepository boardRepository;
    public static final String TEAM_URL = "/api/v1/team-page";
    @GetMapping("/posts/{boardId}")
    public ResponseEntity<BoardRes> getPosts(@PathVariable("boardId") Long boardId, Pageable pageable) {
        Page<PostRes> postsPage = teamPageService.getPostsByBoardId(boardId, pageable);

        if (!postsPage.isEmpty()) {
            Board board = boardRepository.findById(boardId)
                    .orElseThrow(() -> new EntityNotFoundException("board not found"));
            BoardRes res = new BoardRes(board.getId(), board.getName(), postsPage.getContent());
            return ResponseEntity.ok(res);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
