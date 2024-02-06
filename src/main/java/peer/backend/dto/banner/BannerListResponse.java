package peer.backend.dto.banner;

import lombok.Getter;
import peer.backend.entity.banner.Banner;
import peer.backend.entity.banner.BannerStatus;

@Getter
public class BannerListResponse {

    private Long bannerId;
    private BannerStatus bannerStatus;
    private String title;

    public BannerListResponse(Banner banner) {
        this.bannerId = banner.getId();
        this.bannerStatus = banner.getBannerStatus();
        this.title = banner.getTitle();
    }
}
