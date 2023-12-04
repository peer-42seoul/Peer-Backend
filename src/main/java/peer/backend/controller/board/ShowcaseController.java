package peer.backend.controller.board;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import peer.backend.dto.board.ShowcaseListResponse;
import peer.backend.service.board.team.ShowcaseService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/showcase")
@Slf4j
public class ShowcaseController {

    private final ShowcaseService showcaseService;

    @GetMapping("/")
    public Page<ShowcaseListResponse> getShowcaseList(@RequestParam int page, @RequestParam int pageSize, Authentication auth){
        return showcaseService.getShowCaseList(page, pageSize, auth);
    }


    @PostMapping("/favorite/{id}")
    public void doFavorite(@PathVariable Long id, Authentication auth){
        showcaseService.doFavorite(id, auth);
    }

    @PostMapping("like/{id}")
    public void doLike(@PathVariable Long id, Authentication auth){
        showcaseService.doLike(id, auth);
    }

}
