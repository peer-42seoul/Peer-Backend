package peer.backend.service;

import java.util.Objects;
import java.util.UUID;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import peer.backend.dto.banner.CreateBannerRequest;
import peer.backend.entity.banner.Banner;
import peer.backend.entity.banner.BannerReservationType;
import peer.backend.entity.banner.BannerStatus;
import peer.backend.exception.ConflictException;
import peer.backend.repository.banner.BannerRepository;
import peer.backend.service.file.ObjectService;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;
    private final ObjectService objectService;
    private final UtilService utilService;

    @Transactional
    public void createBanner(CreateBannerRequest request) {
        Banner banner = this.createBannerFromCreateBannerRequest(request);
        this.bannerRepository.save(banner);
    }

    private Banner createBannerFromCreateBannerRequest(CreateBannerRequest request) {
        String imageUrl = this.uploadBannerImage(request.getImage());

        Banner banner = Banner.builder()
            .bannerType(request.getBannerType())
            .imageUrl(imageUrl)
            .bannerStatus(
                this.getBannerStatusFromBannerReservationType(request.getBannerReservationType()))
            .noticeUrl(request.getNoticeUrl())
            .build();

        if (request.getBannerReservationType().equals(BannerReservationType.RESERVATION)
            && Objects.nonNull(request.getReservationDate())) {
            if (!this.utilService.checkDatePastNow(request.getReservationDate())) {
                throw new ConflictException("예약 시간이 현재보다 이후여야 합니다!");
            }
            banner.setReservationDate(request.getReservationDate());
        }

        return banner;
    }

    private BannerStatus getBannerStatusFromBannerReservationType(BannerReservationType type) {
        if (type.equals(BannerReservationType.IMMEDIATELY)) {
            return BannerStatus.ONGOING;
        } else {
            return BannerStatus.RESERVATION;
        }
    }

    private String uploadBannerImage(String imageData) {
        String bannerImageFolder = "banner/";
        return this.objectService.uploadObject(bannerImageFolder + UUID.randomUUID(),
            imageData, "image");
    }
}
