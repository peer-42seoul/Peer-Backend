package peer.backend.controller.board;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import peer.backend.dto.board.team.*;
import peer.backend.service.board.team.ShowcaseService;
import peer.backend.service.profile.UserPortfolioService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/showcase")
@Slf4j
public class ShowcaseController {

    private final ShowcaseService showcaseService;
    private final UserPortfolioService userPortfolioService;

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
    public ResponseEntity<Object> updateShowcase(@PathVariable Long showcaseId, @RequestBody ShowcaseUpdateDto request, Authentication auth){
        return showcaseService.updateShowcase(showcaseId, request, auth);
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

}
