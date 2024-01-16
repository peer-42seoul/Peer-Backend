package peer.backend.controller.board;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import peer.backend.dto.board.team.ShowcaseListResponse;
import peer.backend.dto.board.team.ShowcaseResponse;
import peer.backend.dto.board.team.ShowcaseWriteResponse;
import peer.backend.service.board.team.ShowcaseService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/showcase")
@Slf4j
public class ShowcaseController {

    private final ShowcaseService showcaseService;

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

    @GetMapping("/{teamId}")
    public ShowcaseWriteResponse getTeamInfoForCreateShowcase(@PathVariable Long teamId, Authentication auth){
        return showcaseService.getTeamInfoForCreateShowcase(teamId, auth);
    }

}
