package peer.backend.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.banner.CreateBannerRequest;
import peer.backend.service.BannerService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/banner")
public class BannerController {

    private final BannerService bannerService;

    @PostMapping
    public void createBanner(@RequestBody @Valid CreateBannerRequest request) {
        this.bannerService.createBanner(request);
    }
}
