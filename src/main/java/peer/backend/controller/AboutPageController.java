package peer.backend.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import peer.backend.dto.ContactUsRequest;
import peer.backend.dto.announcement.AboutAnnouncementListResponse;
import peer.backend.dto.announcement.AboutAnnouncementResponse;
import peer.backend.entity.announcement.Announcement;
import peer.backend.entity.announcement.AnnouncementStatus;
import peer.backend.service.AnnouncementService;
import peer.backend.service.ContactUsService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(AboutPageController.CONTACT_US)
public class AboutPageController {
    public static final String CONTACT_US = "api/v1/about";
    private final ContactUsService contactUsService;
    private final AnnouncementService announcementService;

    @ApiOperation(value = "", notes = "Contact Us 로 연락을 수신합니다.")
    @PostMapping("/contact-us")
    public ResponseEntity<Void> receiveContactUs(@RequestBody @Valid ContactUsRequest data) {
        this.contactUsService.saveContactUs(data);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/announcement")
    public ResponseEntity<Page<AboutAnnouncementListResponse>> getAboutAnnouncementList(
            Pageable pageable) {
        Page<Announcement> announcementList = this.announcementService.getAnnouncementListByStatusAndPageable(
                AnnouncementStatus.PUBLISHED, pageable);
        return ResponseEntity.ok(announcementList.map(AboutAnnouncementListResponse::new));
    }

    @GetMapping("/announcement/{announcementId}")
    public ResponseEntity<AboutAnnouncementResponse> getAboutAnnouncement(
            @PathVariable("announcementId") Long announcementId) {
        Announcement announcement = this.announcementService.getAnnouncement(announcementId);
        this.announcementService.increaseView(announcement);
        return ResponseEntity.ok(new AboutAnnouncementResponse(announcement));
    }
}
