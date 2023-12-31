package peer.backend.scheduler;

import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import peer.backend.entity.banner.Banner;
import peer.backend.entity.banner.BannerStatus;
import peer.backend.entity.announcement.Announcement;
import peer.backend.entity.announcement.AnnouncementStatus;
import peer.backend.service.AnnouncementService;
import peer.backend.service.BannerService;
import peer.backend.service.UtilService;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminScheduler {

    private final AnnouncementService announcementService;
    private final BannerService bannerService;
    private final UtilService utilService;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void changeAnnouncementStatus() {
        List<Announcement> announcementList = this.announcementService.getAnnouncementListByAnnouncementStatus(
            AnnouncementStatus.RESERVATION);
        announcementList.forEach(announcement -> {
            if (utilService.isBeforeThanNow(announcement.getReservationDate())) {
                announcement.setAnnouncementStatus(AnnouncementStatus.PUBLISHED);
            }
        });
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void changeBannerStatus() {
        List<Banner> bannerList = this.bannerService.getBannerListByBannerStatus(
            BannerStatus.RESERVATION);
        bannerList.forEach(banner -> {
            if (utilService.isBeforeThanNow(banner.getReservationDate())) {
                banner.setBannerStatus(BannerStatus.ONGOING);
            }
        });
    }
}
