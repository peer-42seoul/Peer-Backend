package peer.backend.controller.board;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import peer.backend.dto.board.team.*;
import peer.backend.entity.board.team.PostAnswer;
import peer.backend.service.board.team.BoardService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/team/board")
public class BoardController {

    private final BoardService boardService;

    @PostMapping("/create")
    public void createBoard(@RequestBody BoardCreateRequest request, Authentication auth) {
        boardService.createBoard(request, auth);
    }

    @PostMapping("/post/create")
    public void createPost(@RequestBody PostCreateRequest request, Authentication auth) {
        boardService.createPost(request, auth);
    }

    @GetMapping("/list/{teamId}")
    public void getBoardList(@PathVariable("teamId") Long teamId, Authentication auth) {
        boardService.getBoardList(teamId, auth);
    }

    @PutMapping("/{boardId}")
    public void updateBoard(@PathVariable("boardId") Long boardId,
                            @RequestBody BoardUpdateRequest request, Authentication auth) {
        boardService.updateBoard(boardId, request, auth);
    }

    @PutMapping("/post/{postId}")
    public void updatePost(@PathVariable("postId") Long postId,
                           @RequestBody PostUpdateRequest request, Authentication auth) {
        boardService.updatePost(postId, request, auth);
    }

    @DeleteMapping("/{boardId}")
    public void deleteBoard(@PathVariable("boardId") Long id, Authentication auth) {
        boardService.deleteBoard(id, auth);

    }

    @DeleteMapping("/post/{postId}")
    public void deletePost(@PathVariable("postId") Long postId, Authentication auth) {
        boardService.deletePost(postId, auth);
    }

    @PostMapping("/post/answer")
    public void createComment(@RequestBody PostCommentRequest request, Authentication auth){
        boardService.createComment(request, auth);
    }
}
