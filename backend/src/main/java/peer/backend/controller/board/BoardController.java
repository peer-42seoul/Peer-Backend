package peer.backend.controller.board;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import peer.backend.dto.board.team.BoardCreateRequest;
import peer.backend.dto.board.team.BoardUpdateRequest;
import peer.backend.dto.board.team.PostCreateRequest;
import peer.backend.dto.board.team.PostUpdateRequest;
import peer.backend.service.board.team.BoardService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/team/board")
public class BoardController {

    private final BoardService boardService;

    @PostMapping("/create")
    public void createBoard(@RequestBody BoardCreateRequest request, Authentication auth){
        boardService.createBoard(request, auth);
    }

    @PostMapping("/post/create")
    public void createPost(@RequestBody PostCreateRequest request, Authentication auth){
        boardService.createPost(request, auth);
    }

    @GetMapping("/list/{team_id}")
    public void getBoardList(@PathVariable("team_id") Long team_id, Authentication auth){
        boardService.getBoardList(team_id, auth);
    }

    @PutMapping("/{board_id}")
    public void updateBoard(@PathVariable("board_id") Long board_id, @RequestBody BoardUpdateRequest request, Authentication auth){
        boardService.updateBoard(board_id, request, auth);
    }

    @PutMapping("/post/{post_id}")
    public void updatePost(@PathVariable("post_id") Long post_id, @RequestBody PostUpdateRequest request, Authentication auth){
        boardService.updatePost(post_id, request, auth);
    }

    @DeleteMapping("/{boardId}")
    public void deleteBoard(@PathVariable("boardId") Long id, Authentication auth){
        boardService.deleteBoard(id, auth);

    }

    @DeleteMapping("/post/{post_id}")
    public void deletePost(@PathVariable("post_id") Long post_id, Authentication auth){
        boardService.deletePost(post_id, auth);
    }
}
