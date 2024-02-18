package peer.backend.controller.board;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import peer.backend.dto.board.team.*;
import peer.backend.dto.noti.enums.NotificationPriority;
import peer.backend.dto.noti.enums.NotificationType;
import peer.backend.entity.board.team.Post;
import peer.backend.entity.board.team.enums.BoardType;
import peer.backend.entity.user.User;
import peer.backend.service.board.team.BoardService;
import peer.backend.service.board.team.ShowcaseService;
import peer.backend.service.noti.NotificationCreationService;
import peer.backend.service.profile.UserPortfolioService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/showcase")
@Slf4j
public class ShowcaseController {

    private final ShowcaseService showcaseService;
    private final UserPortfolioService userPortfolioService;
    private final BoardService boardService;

    private final NotificationCreationService notificationCreationService;

    @GetMapping("")
    public Page<ShowcaseListResponse> getShowcaseList(@RequestParam int page, @RequestParam int pageSize, Authentication auth){
        return showcaseService.getShowCaseList(page - 1, pageSize, auth);
    }


    @PostMapping("/favorite/{id}")
    public boolean doFavorite(@PathVariable Long id, Authentication auth){
        return showcaseService.doFavorite(id, auth);
    }

    @PostMapping("/like/{id}")
    public int doLike(@PathVariable Long id, Authentication auth){
        return showcaseService.doLike(id, auth);
    }

    @GetMapping("/{showcaseId}")
    public ShowcaseResponse getShowcase(@PathVariable Long showcaseId, Authentication auth){
        return showcaseService.getShowcase(showcaseId, auth);
    }

    @PutMapping("/edit/{showcaseId}")
    public ResponseEntity<Object> updateShowcase(@PathVariable Long showcaseId, @RequestBody @Valid ShowcaseUpdateDto request, Authentication auth){
        return showcaseService.updateShowcase(showcaseId, request, User.authenticationToUser(auth));
    }

    @GetMapping("/write/{teamId}")
    public ShowcaseWriteResponse getTeamInfoForCreateShowcase(@PathVariable Long teamId, Authentication auth){
        return showcaseService.getTeamInfoForCreateShowcase(teamId, auth);
    }

    @PostMapping("/write")
    public Long createShowcase(@RequestBody @Valid ShowcaseCreateDto request, Authentication auth) {
        Long result =  showcaseService.createShowcase(request, auth);
        this.userPortfolioService.setWholeTeamUserWithShowcaseCreation(request.getTeamId(), result);
        return result;
    }

    @DeleteMapping("/{showcaseId}")
    public ResponseEntity<Object> deleteShowcase(@PathVariable Long showcaseId, Authentication auth){
        return showcaseService.deleteShowcase(showcaseId, User.authenticationToUser(auth));
    }

    @GetMapping("/page/{teamId}")
    public ShowcasePageInfoResponse getShowcasePageInfo(@PathVariable Long teamId, Authentication auth){
        return showcaseService.getShowcasePageInfo(teamId, User.authenticationToUser(auth));
    }

    @PostMapping("/public/{showcaseId}")
    public boolean changeShowcasePublic(@PathVariable Long showcaseId, Authentication auth){
        return showcaseService.changeShowcasePublic(showcaseId, User.authenticationToUser(auth));
    }

    @GetMapping("/comment/{showcaseId}")
    public List<PostCommentListResponse> getShowcaseComments(@PathVariable Long showcaseId, Authentication auth){
        try {
            User user = User.authenticationToUser(auth);
            return boardService.getComments(showcaseId, user, BoardType.SHOWCASE);
        } catch (Exception e) {
            return boardService.getComments(showcaseId, null, BoardType.SHOWCASE);
        }
    }

    //TODO : DTO 수정 필요...!!
    @PostMapping("/comment")
    public void createShowcaseComment(@RequestBody @Valid PostCommentRequest request, Authentication auth){
        User user = User.authenticationToUser(auth);
        Post data = boardService.createComment(
                request.getPostId(),
                request.getContent(),
                user,
                BoardType.SHOWCASE);
        this.notificationCreationService.makeNotificationForTeam(
                null,
                user.getNickname() + "님께서 "+ data.getTitle() +" 에 댓글을 다셨습니다 : " + request.getContent(),
                "/showcase/detail/" + request.getPostId(),
                NotificationPriority.IMMEDIATE,
                NotificationType.SYSTEM,
                null,
                data.getOwnTeamId(),
                null
        );
    }

    @DeleteMapping("/comment/{commentId}")
    public void deleteShowcaseComment(@PathVariable Long commentId, Authentication auth){
        boardService.deleteComment(commentId, User.authenticationToUser(auth));
    }

    @PutMapping("/comment/{commentId}")
    public void updateShowcaseComment(@PathVariable Long commentId, @RequestBody @Valid PostCommentUpdateRequest request, Authentication auth){
        boardService.updateComment(commentId, request, User.authenticationToUser(auth));
    }
}
