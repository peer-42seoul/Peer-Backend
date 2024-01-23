package peer.backend.controller.team;

import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import peer.backend.dto.team.PostDetail;
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

    @ApiOperation(value = "TEAM-PAGE", notes = "특정 게시판에 검색된 글 목록을 가져옵니다.")
    @GetMapping("/posts/{boardId}")
    public ResponseEntity<Page<PostRes>> getPostsByKeyword(@PathVariable("boardId") Long boardId, Pageable pageable,
                                                      @RequestParam(value = "keyword") String keyword) {
        Pageable pageReq = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize());
        Page<PostRes> postsPage = teamPageService.getPostsByBoardIdWithKeyword(boardId, pageReq, keyword);

        if (!postsPage.isEmpty()) {
            return ResponseEntity.ok(postsPage);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @ApiOperation(value = "TEAM-PAGE", notes = "특정 게시판 특정 글을 가져옵니다.")
    @GetMapping("/post/{postId}")
    public ResponseEntity<PostDetail> getPost(@PathVariable("postId") Long postId) {
        Post post = teamPageService.getPostById(postId);
        PostDetail res = new PostDetail(post.getId(), post.getTitle(), post.getUser().getNickname(), post.getContent(),
                post.getHit(), post.getCreatedAt());
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

    @ApiOperation(value = "TEAM-PAGE-NOTICE", notes = "공지사항 게시판에 공지사항 글 게시.")
    @PostMapping("/notice/create")
    public ResponseEntity createNoticePost(@RequestBody PostCreateRequest postCreateRequest, Authentication auth) {
        teamPageService.createNoticePost(postCreateRequest, auth);
        return ResponseEntity.ok().build();
    }
}
