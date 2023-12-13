package peer.backend.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.blacklist.AddBlacklistRequest;
import peer.backend.dto.blacklist.BlacklistResponse;
import peer.backend.dto.blacklist.HandleBlacklistRequest;
import peer.backend.entity.blacklist.Blacklist;
import peer.backend.entity.blacklist.BlacklistHandleType;
import peer.backend.service.blacklist.BlacklistService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/blacklist")
public class BlacklistController {

    private final BlacklistService blacklistService;

    @GetMapping()
    Page<BlacklistResponse> getBlacklist(Pageable pageable) {
        Page<Blacklist> blacklist = this.blacklistService.getBlacklists(pageable);
        return blacklist.map(BlacklistResponse::new);
    }

    @PostMapping()
    void addBlacklist(@RequestBody @Valid AddBlacklistRequest request) {
        this.blacklistService.addBlacklistToEmail(request.getEmail(), request.getType(),
            request.getContent());
    }

    @PostMapping("/handle")
    void handleBlacklist(@RequestBody @Valid HandleBlacklistRequest request) {
        if (request.getType().equals(BlacklistHandleType.FREE)) {
            this.blacklistService.deleteBlacklist(request.getBlacklistId());
        }
    }
}
