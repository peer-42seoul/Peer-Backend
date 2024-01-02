package peer.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import peer.backend.dto.banner.CreateBannerRequest;
import peer.backend.dto.banner.UpdateBannerRequest;
import peer.backend.entity.banner.Banner;
import peer.backend.entity.banner.BannerReservationType;
import peer.backend.entity.banner.BannerStatus;
import peer.backend.exception.ConflictException;
import peer.backend.exception.NotFoundException;
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

    @Transactional
    public Page<Banner> getBannerList(Pageable pageable) {
        return this.bannerRepository.findAll(pageable);
    }

    @Transactional
    public Banner getBanner(Long id) {
        return this.bannerRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 배너 ID 입니다."));
    }

    @Transactional
    public void deleteBanner(Long id) {
        Banner banner = this.getBanner(id);
        this.bannerRepository.deleteById(id);
        this.objectService.deleteObject(banner.getImageUrl());
    }

    @Transactional
    public void updateBanner(UpdateBannerRequest request) {
        Banner banner = this.getBanner(request.getBannerId());
        this.updateBannerFromUpdateBannerRequest(banner, request);
    }

    @Transactional
    public void setBannerStatus(Long bannerId, BannerStatus status) {
        Banner banner = this.getBanner(bannerId);
        if (status.equals(BannerStatus.ONGOING) && banner.getBannerStatus()
            .equals(BannerStatus.ONGOING)) {
            throw new ConflictException("이미 게재 상태인 배너를 게재할 순 없습니다!");
        } else if (status.equals(BannerStatus.TERMINATION) && banner.getBannerStatus()
            .equals(BannerStatus.TERMINATION)) {
            throw new ConflictException("이미 종료 상태인 배너를 종료할 순 없습니다!");
        }
        banner.setBannerStatus(status);
    }

    @Transactional
    public List<Banner> getBannerListByBannerStatus(BannerStatus status) {
        return this.bannerRepository.findAllByBannerStatus(status);
    }

    private Banner createBannerFromCreateBannerRequest(CreateBannerRequest request) {
        String imageUrl = this.uploadBannerImage(request.getImage());

        Banner banner = Banner.builder()
            .bannerType(request.getBannerType())
            .title(request.getTitle())
            .imageUrl(imageUrl)
            .bannerStatus(
                this.getBannerStatusFromBannerReservationType(request.getBannerReservationType()))
            .noticeUrl(request.getNoticeUrl())
            .build();

        if (request.getBannerReservationType().equals(BannerReservationType.RESERVATION)
            && Objects.nonNull(request.getReservationDate())) {
            this.setBannerReservationDate(banner, request.getReservationDate());
        }

        return banner;
    }

    private void updateBannerFromUpdateBannerRequest(Banner banner, UpdateBannerRequest request) {
        banner.setBannerType(request.getBannerType());
        banner.setTitle(request.getTitle());
        banner.setNoticeUrl(request.getNoticeUrl());
        // BannerReservationType이 즉시일 경우
        if (request.getBannerReservationType().equals(BannerReservationType.IMMEDIATELY)) {
            // Banner의 상태가 예약일 경우
            if (banner.getBannerStatus().equals(BannerStatus.RESERVATION)) {
                banner.setBannerStatus(BannerStatus.ONGOING);
                // Banner의 상태가 예약이 아닐 경우
            } else {
                throw new ConflictException("예약 상태가 아닌 배너의 예약 타입을 즉시로 설정할 수 없습니다!");
            }
            // BannerReservationType이 예약일 경우
        } else {
            // Banner의 상태가 예약일 경우
            if (banner.getBannerStatus().equals(BannerStatus.RESERVATION)) {
                this.setBannerReservationDate(banner, request.getReservationDate());
                // Banner의 상태가 예약이 아닐 경우
            } else {
                throw new ConflictException("이미 진행 중인 배너의 예약 타입을 예약으로 설정할 수 없습니다!");
            }
        }
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

    private void setBannerReservationDate(Banner banner, LocalDateTime date) {
        if (this.utilService.isBeforeThanNow(date)) {
            throw new ConflictException("예약 시간이 현재보다 이후여야 합니다!");
        }
        banner.setReservationDate(date);
    }
}
