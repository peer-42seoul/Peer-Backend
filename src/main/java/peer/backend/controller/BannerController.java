package peer.backend.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.banner.BannerIdRequest;
import peer.backend.dto.banner.BannerListResponse;
import peer.backend.dto.banner.BannerResponse;
import peer.backend.dto.banner.CreateBannerRequest;
import peer.backend.dto.notice.NoticeResponse;
import peer.backend.entity.banner.Banner;
import peer.backend.entity.banner.BannerStatus;
import peer.backend.entity.notice.Notice;
import peer.backend.service.BannerService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/banner")
public class BannerController {

    private final BannerService bannerService;

    // TODO: 예약된 배너 스케쥴링 처리 추가 필요.
    @PostMapping
    public void createBanner(@RequestBody @Valid CreateBannerRequest request) {
        this.bannerService.createBanner(request);
    }

    @GetMapping
    public Page<BannerListResponse> getBannerList(Pageable pageable) {
        Page<Banner> bannerList = this.bannerService.getBannerList(pageable);
        return bannerList.map(BannerListResponse::new);
    }

    @GetMapping("{bannerId}")
    public BannerResponse getBanner(@PathVariable("bannerId") Long bannerId) {
        Banner banner = this.bannerService.getBanner(bannerId);
        return new BannerResponse(banner);
    }

    @DeleteMapping
    public void deleteBanner(@RequestBody @Valid BannerIdRequest request) {
        this.bannerService.deleteBanner(request.getBannerId());
    }

    @PostMapping("publish")
    public void publishBanner(@RequestBody @Valid BannerIdRequest request) {
        this.bannerService.setBannerStatus(request.getBannerId(), BannerStatus.ONGOING);
    }
}
