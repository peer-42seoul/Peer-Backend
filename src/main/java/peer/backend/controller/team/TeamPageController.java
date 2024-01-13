package peer.backend.controller.team;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.team.BoardRes;
import peer.backend.entity.board.team.Board;
import peer.backend.entity.board.team.Post;
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
    public BoardRes getPosts(@PathVariable("boardId") Long boardId, Pageable pageable) {
//        Page<Post> posts = teamPageService.getPostListByBoardId(pageable, boardId);
        Optional<Board> boardOptional = boardRepository.findById(boardId);

        if (boardOptional.isPresent()) {
            Board board = boardOptional.get();

            // 페이지네이션 적용을 위해 필요한 경우 추가 로직 구현
            // 예: Page<Post> posts = someMethodToApplyPagination(board.getPosts(), pageable);

            return new BoardRes(board.getId(), board.getName(), (Page<Post>) board.getPosts());
        } else {
            System.out.println("posts is empty");
            Board board = boardRepository.findById(boardId).orElseThrow();
            return new BoardRes(board.getId(), board.getName(), (Page<Post>) board.getPosts());
        }
    }

}
