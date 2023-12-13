package peer.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.blacklist.BlacklistResponse;
import peer.backend.entity.blacklist.Blacklist;
import peer.backend.service.blacklist.BlacklistService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/blacklist")
public class BlacklistController {

    private final BlacklistService blacklistService;

    @GetMapping()
    Page<BlacklistResponse> getBlacklist(Pageable pageable) {
        Page<Blacklist> blacklist = this.blacklistService.getBlacklist(pageable);
        return blacklist.map(BlacklistResponse::new);
    }
}
