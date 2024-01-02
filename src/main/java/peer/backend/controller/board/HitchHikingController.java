package peer.backend.controller.board;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.board.recruit.HitchListResponse;
import peer.backend.dto.board.recruit.HitchResponse;
import peer.backend.service.board.recruit.HitchHikingService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hitch")
public class HitchHikingController {

    private HitchHikingService hitchHikingService;

    @GetMapping("")
    public Page<HitchListResponse> getHitchList(@PathVariable int page,
                                                @PathVariable int pageSize,
                                                @PathVariable String type,
                                                Authentication auth){
        return hitchHikingService.getHitchList(page, pageSize, type, auth);
    }
    @GetMapping("{hitchId}")
    public HitchResponse getHitch(){}
    public void HitchLike(){}
    public void HitchDislike(){}
}