package peer.backend.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.announcement.AboutAnnouncementListResponse;
import peer.backend.dto.announcement.AboutAnnouncementResponse;
import peer.backend.dto.announcement.AnnouncementIdRequest;
import peer.backend.dto.announcement.AnnouncementListResponse;
import peer.backend.dto.announcement.AnnouncementResponse;
import peer.backend.dto.announcement.CreateAnnouncementRequest;
import peer.backend.dto.announcement.UpdateAnnouncementRequest;
import peer.backend.entity.announcement.Announcement;
import peer.backend.entity.announcement.AnnouncementStatus;
import peer.backend.service.AnnouncementService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    // TODO: 알림 enum이 예약일 경우 알림 예약 걸고(mayby spring batch?), 공지사항도 예약 걸어야됨.
    @PostMapping("/admin/announcement")
    public void writeAnnouncement(@RequestBody @Valid CreateAnnouncementRequest request) {
        this.announcementService.writeAnnouncement(request);
    }

    @GetMapping("/admin/announcement")
    public Page<AnnouncementListResponse> getAnnouncementList(Pageable pageable) {
        Page<Announcement> announcementList = this.announcementService.getAnnouncementList(
            pageable);
        return announcementList.map(AnnouncementListResponse::new);
    }

    @GetMapping("/admin/announcement/{announcementId}")
    public AnnouncementResponse getAnnouncement(
        @PathVariable("announcementId") Long announcementId) {
        Announcement announcement = this.announcementService.getAnnouncement(announcementId);
        this.announcementService.increaseView(announcement);
        return new AnnouncementResponse(announcement);
    }

    @DeleteMapping("/admin/announcement")
    public void deleteAnnouncement(@RequestBody @Valid AnnouncementIdRequest request) {
        this.announcementService.deleteAnnouncement(request.getAnnouncementId());
    }

    // TODO: 지금은 단순 데이터를 수정만 하지만 알림이 추가되면 알림 여부에 따라 알림을 보낼지말지 추가되야함
    @PutMapping("/admin/announcement")
    public void updateAnnouncement(@RequestBody @Valid UpdateAnnouncementRequest request) {
        this.announcementService.updateAnnouncement(request);
    }

    @PostMapping("/admin/announcement/hide")
    public void hideAnnouncement(@RequestBody @Valid AnnouncementIdRequest request) {
        this.announcementService.setAnnouncementStatus(request.getAnnouncementId(),
            AnnouncementStatus.HIDING);
    }

    @PostMapping("/admin/announcement/show")
    public void showAnnouncement(@RequestBody @Valid AnnouncementIdRequest request) {
        this.announcementService.setAnnouncementStatus(request.getAnnouncementId(),
            AnnouncementStatus.PUBLISHED);
    }

//    @GetMapping("/about/announcement")
//    public ResponseEntity<Page<AboutAnnouncementListResponse>> getAboutAnnouncementList(
//        Pageable pageable) {
//        Page<Announcement> announcementList = this.announcementService.getAnnouncementListByStatusAndPageable(
//            AnnouncementStatus.PUBLISHED, pageable);
//        return ResponseEntity.ok(announcementList.map(AboutAnnouncementListResponse::new));
//    }
//
//    @GetMapping("/about/announcement/{announcementId}")
//    public ResponseEntity<AboutAnnouncementResponse> getAboutAnnouncement(
//        @PathVariable("announcementId") Long announcementId) {
//        Announcement announcement = this.announcementService.getAnnouncement(announcementId);
//        this.announcementService.increaseView(announcement);
//        return ResponseEntity.ok(new AboutAnnouncementResponse(announcement));
//    }
}
