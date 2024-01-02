package peer.backend.dto.banner;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDateTime;
import lombok.Getter;
import peer.backend.entity.BaseEntity;
import peer.backend.entity.banner.Banner;
import peer.backend.entity.banner.BannerStatus;
import peer.backend.entity.banner.BannerType;

@Getter
public class BannerResponse {

    private final BannerStatus bannerStatus;
    private final BannerType bannerType;
    private final String title;
    private final String image;
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime date;
    private final String noticeUrl;

    public BannerResponse(Banner banner) {
        this.bannerStatus = banner.getBannerStatus();
        this.bannerType = banner.getBannerType();
        this.title = banner.getTitle();
        this.image = banner.getImageUrl();
        this.noticeUrl = banner.getNoticeUrl();
        if (this.bannerStatus.equals(BannerStatus.RESERVATION)) {
            this.date = banner.getReservationDate();
        } else {
            this.date = banner.getCreatedAt();
        }
    }
}
