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
import peer.backend.dto.banner.BannerListResponse;
import peer.backend.dto.banner.CreateBannerRequest;
import peer.backend.entity.banner.Banner;
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

    @GetMapping
    public Page<BannerListResponse> getBannerList(Pageable pageable) {
        Page<Banner> bannerList = this.bannerService.getBannerList(pageable);
        return bannerList.map(BannerListResponse::new);
    }
}
