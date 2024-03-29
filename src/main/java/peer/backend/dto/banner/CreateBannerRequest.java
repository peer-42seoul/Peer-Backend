package peer.backend.dto.banner;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Getter;
import peer.backend.annotation.ValidEnum;
import peer.backend.entity.banner.BannerReservationType;
import peer.backend.entity.banner.BannerType;

@Getter
public class CreateBannerRequest {

    @ValidEnum(enumClass = BannerType.class)
    private BannerType bannerType;

    @NotBlank(message = "배너 제목을 필수입니다.")
    @Size(max = 30, message = "길이는 30 이하여야합니다.")
    private String title;

    @NotBlank(message = "이미지는 필수입니다.")
    private String image;

    @ValidEnum(enumClass = BannerReservationType.class)
    private BannerReservationType bannerReservationType;

    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime reservationDate;

    @Size(max = 1000, message = "길이는 1000 이하여야합니다.")
    private String announcementUrl;
}
