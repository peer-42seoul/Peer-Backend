package peer.backend.controller.board;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import peer.backend.dto.board.recruit.HitchListResponse;
import peer.backend.dto.board.recruit.HitchResponse;
import peer.backend.entity.board.recruit.enums.RecruitFavoriteEnum;
import peer.backend.entity.user.User;
import peer.backend.service.board.recruit.HitchHikingService;
import peer.backend.service.board.recruit.RecruitService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hitch")
public class HitchHikingController {

    private final HitchHikingService hitchHikingService;
    private final RecruitService recruitService;

    @GetMapping("")
    public Page<HitchListResponse> getHitchList(@RequestParam int page,
                                                @RequestParam int pageSize,
                                                @RequestParam String type,
                                                Authentication auth){
        try {
            User user = User.authenticationToUser(auth);
            return hitchHikingService.getHitchList(page, pageSize, type, user.getId());
        } catch (Exception e) {
            return hitchHikingService.getHitchList(page, pageSize, type, null);
        }
    }

    @GetMapping("/{hitchId}")
    public HitchResponse getHitch(@PathVariable Long hitchId){
        return hitchHikingService.getHitch(hitchId);

    }
    @PostMapping("/dislike/{recruitId}")
    public void hitchDislike(@PathVariable Long recruitId, Authentication auth){
        recruitService.changeRecruitFavorite(auth, recruitId, RecruitFavoriteEnum.DISLIKE);
    }
}