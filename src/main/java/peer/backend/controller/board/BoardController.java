package peer.backend.controller.board;

import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.board.team.BoardCreateRequest;
import peer.backend.dto.board.team.BoardUpdateRequest;
import peer.backend.dto.board.team.PostCommentListResponse;
import peer.backend.dto.board.team.PostCommentRequest;
import peer.backend.dto.board.team.PostCommentUpdateRequest;
import peer.backend.dto.board.team.PostCreateRequest;
import peer.backend.dto.board.team.PostUpdateRequest;
import peer.backend.entity.user.User;
import peer.backend.service.board.team.BoardService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/team")
public class BoardController {

    private final BoardService boardService;

    @PostMapping("/board/create")
    public void createBoard(@RequestBody BoardCreateRequest request, Authentication auth) {
        boardService.createBoard(request, auth);
    }

    @PostMapping("/post/create")
    public void createPost(@RequestBody PostCreateRequest request, Authentication auth) {
        boardService.createPost(request, auth);
    }

    @GetMapping("/board/list/{teamId}")
    public void getBoardList(@PathVariable("teamId") Long teamId, Authentication auth) {
        boardService.getBoardList(teamId, auth);
    }

    @PutMapping("/board/{boardId}")
    public void updateBoard(@PathVariable("boardId") Long boardId,
                            @RequestBody BoardUpdateRequest request, Authentication auth) {
        boardService.updateBoard(boardId, request, auth);
    }

    @PutMapping("/post/{postId}")
    public void updatePost(@PathVariable("postId") Long postId,
                           @RequestBody PostUpdateRequest request, Authentication auth) {
        boardService.updatePost(postId, request, auth);
    }

    @DeleteMapping("/board/{boardId}")
    public void deleteBoard(@PathVariable("boardId") Long id, Authentication auth) {
        boardService.deleteBoard(id, auth);

    }

    @DeleteMapping("/post/{postId}")
    public void deletePost(@PathVariable("postId") Long postId, Authentication auth) {
        boardService.deletePost(postId, auth);
    }

    @PostMapping("/post/comment")
    public void createComment(@RequestBody @Valid PostCommentRequest request, Authentication auth) {
        boardService.createComment(request, auth);
    }

    @PutMapping("/post/comment/{commentId}")
    public void updateComment(@PathVariable Long commentId,
                              @RequestBody @Valid PostCommentUpdateRequest request,
                              Authentication auth) {
        boardService.updateComment(commentId, request, auth);
    }

    @GetMapping("/post/comment/{postId}")
    public List<PostCommentListResponse> getComments(@PathVariable Long postId, Authentication auth) {
        User user = User.authenticationToUser(auth);
        return boardService.getComments(postId, user.getId());
    }

    @DeleteMapping("/post/comment/{commentId}")
    public ResponseEntity<Object> deleteComment(@PathVariable Long commentId, Authentication auth) {
        return boardService.deleteComment(commentId, auth);
    }
}
