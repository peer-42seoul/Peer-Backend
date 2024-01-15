package peer.backend.controller.team;

import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.board.team.PostCreateRequest;
import peer.backend.dto.team.BoardRes;
import peer.backend.dto.team.PostRes;
import peer.backend.dto.team.SimpleBoardRes;
import peer.backend.entity.board.team.Board;
import peer.backend.entity.board.team.Post;
import peer.backend.service.board.team.BoardService;
import peer.backend.service.teampage.TeamPageService;

@RestController
@RequiredArgsConstructor
@RequestMapping(TeamPageController.TEAM_URL)
public class TeamPageController {
    private final TeamPageService teamPageService;
    private final BoardService boardService;
    public static final String TEAM_URL = "/api/v1/team-page";

    @ApiOperation(value = "TEAM-PAGE", notes = "특정 게시판 글 목록을 가져옵니다.")
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

    @ApiOperation(value = "TEAM-PAGE", notes = "특정 게시판에 검색된 글 목록을 가져옵니다.")
    @GetMapping("/posts/search/{boardId}")
    public ResponseEntity<BoardRes> getPostsByKeyword(@PathVariable("boardId") Long boardId, Pageable pageable, @RequestParam(value = "keyword") String keyword) {
        Page<PostRes> postsPage = teamPageService.getPostsByBoardIdWithKeyword(boardId, pageable, keyword);

        if (!postsPage.isEmpty()) {
            Board board = boardService.getBoardById(boardId);
            BoardRes res = new BoardRes(board.getId(), board.getName(), postsPage.getContent());
            return ResponseEntity.ok(res);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @ApiOperation(value = "TEAM-PAGE", notes = "특정 게시판 특정 글을 가져옵니다.")
    @GetMapping("/post/{postId}")
    public ResponseEntity<PostRes> getPost(@PathVariable("postId") Long postId) {
        Post post = teamPageService.getPostById(postId);
        System.out.println(post);
        PostRes res = new PostRes(post.getId(), post.getTitle(), post.getUser().getNickname(), post.getHit(),
                post.getCreatedAt());
        return ResponseEntity.ok(res);
    }

    @ApiOperation(value = "TEAM-PAGE", notes = "특정 팀에 게시판 목록을 가져옵니다.")
    @GetMapping("/simple/{teamId}")
    public ResponseEntity<List<SimpleBoardRes>> getSimpleBoardList(@PathVariable("teamId") Long teamId,
                                                                   Authentication auth) {
        List<SimpleBoardRes> boards = boardService.getSimpleBoards(teamId, auth);
        return ResponseEntity.ok(boards);
    }

    @ApiOperation(value = "TEAM-PAGE", notes = "특정 게시판에 일반 작성 글 게시.")
    @PostMapping("/posts/create")
    public ResponseEntity createGeneralPost(@RequestBody PostCreateRequest postCreateRequest, Authentication auth) {
        teamPageService.createGeneralPost(postCreateRequest, auth);
        return ResponseEntity.ok().build();
    }
}
