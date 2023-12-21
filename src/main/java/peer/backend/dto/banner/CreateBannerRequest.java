package peer.backend.dto.banner;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import peer.backend.annotation.ValidEnum;
import peer.backend.entity.banner.BannerReservationType;
import peer.backend.entity.banner.BannerType;

@Getter
public class CreateBannerRequest {

    @ValidEnum(enumClass = BannerType.class)
    private BannerType bannerType;

    @NotBlank(message = "이미지는 필수입니다.")
    private String image;

    @ValidEnum(enumClass = BannerReservationType.class)
    private BannerReservationType bannerReservationType;

    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime reservationDate;

    private String noticeUrl;
}
