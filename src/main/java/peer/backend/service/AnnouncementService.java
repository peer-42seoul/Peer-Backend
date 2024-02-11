package peer.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import peer.backend.dto.announcement.CreateAnnouncementRequest;
import peer.backend.dto.announcement.UpdateAnnouncementRequest;
import peer.backend.dto.noti.enums.NotificationPriority;
import peer.backend.dto.noti.enums.NotificationType;
import peer.backend.entity.announcement.Announcement;
import peer.backend.entity.announcement.AnnouncementStatus;
import peer.backend.entity.announcement.AnnouncementNoticeStatus;
import peer.backend.exception.ConflictException;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.announcement.AnnouncementRepository;
import peer.backend.service.file.ObjectService;
import peer.backend.service.noti.NotificationCreationService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final ObjectService objectService;
    private final UtilService utilService;

    private final NotificationCreationService notificationCreationService;

    @Transactional
    public void writeAnnouncement(CreateAnnouncementRequest request) {
        Announcement announcement = this.createAnnouncementFromCreateAnnouncementRequest(request);
        this.announcementRepository.save(announcement);

        // 공지사항 글 관련 알림 전달
        this.notificationCreationService.makeNotificationForALL(
                null,
                request.getTitle() + "라는 공지사항이 올라왔습니다! 확인해주세요.",
                null,
                NotificationPriority.SCHEDULED,
                NotificationType.SYSTEM,
                request.getReservationDate(),
                null
        );
    }

    @Transactional
    public Page<Announcement> getAnnouncementList(Pageable pageable) {
        return this.announcementRepository.findAllByOrderByIdDesc(pageable);
    }

    @Transactional
    public Announcement getAnnouncement(Long announcementId) {
        return this.announcementRepository.findById(announcementId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 공지사항 Id 입니다."));
    }

    @Transactional
    public List<Announcement> getAnnouncementListByAnnouncementStatus(AnnouncementStatus status) {
        return this.announcementRepository.findAllByAnnouncementStatus(status);
    }

    @Transactional
    public void deleteAnnouncement(Long announcementId) {
        Announcement announcement = this.getAnnouncement(announcementId);
        this.announcementRepository.deleteById(announcementId);
        this.objectService.deleteObject(announcement.getImage());
    }

    @Transactional
    public void updateAnnouncement(UpdateAnnouncementRequest request) {
        Announcement announcement = this.announcementRepository.findById(
                request.getAnnouncementId())
            .orElseThrow(() -> new NotFoundException("존재하지 않는 공지사항 ID 입니다."));
        this.updateAnnouncementFromRequest(announcement, request);
    }

    private Announcement createAnnouncementFromCreateAnnouncementRequest(
        CreateAnnouncementRequest request) {
        String imageUrl = this.uploadAnnouncementImage(request.getImage());

        Announcement announcement = Announcement.builder()
            .title(request.getTitle())
            .writer(request.getWriter())
            .content(request.getContent())
            .announcementStatus(
                this.getAnnouncementStatusFromAnnouncementNoticeStatus(
                    request.getAnnouncementNoticeStatus()))
            .announcementNoticeStatus(request.getAnnouncementNoticeStatus())
            .image(imageUrl)
            .view(0L)
            .build();

        if (request.getAnnouncementNoticeStatus().equals(AnnouncementNoticeStatus.RESERVATION)
            && Objects.nonNull(
            request.getReservationDate())) {
            this.setAnnouncementReservationDate(announcement, request.getReservationDate());
        }

        return announcement;
    }


    @Transactional
    public void updateAnnouncementFromRequest(Announcement announcement,
        UpdateAnnouncementRequest request) {
        announcement.setTitle(request.getTitle());
        announcement.setWriter(request.getWriter());
        announcement.setContent(request.getContent());
        // notification이 수정됐다!!
        if (!announcement.getAnnouncementNoticeStatus()
            .equals(request.getAnnouncementNoticeStatus())) {
            // 공지사항이 게재 or 숨김 상태일 경우
            if (announcement.getAnnouncementStatus().equals(
                AnnouncementStatus.PUBLISHED) || announcement.getAnnouncementStatus()
                .equals(AnnouncementStatus.HIDING)) {
                throw new ConflictException("공지사항이 게재거나 숨김 상태일때는 알림 여부를 변경할 수 없습니다.");
                // 공지사항이 예약 상태일 경우
            } else {
                // 알림 여부 없음으로 변경
                if (request.getAnnouncementNoticeStatus().equals(AnnouncementNoticeStatus.NONE)) {
                    announcement.setAnnouncementStatus(AnnouncementStatus.PUBLISHED);
                    // 알림 여부 즉시로 변경
                } else if (request.getAnnouncementNoticeStatus()
                    .equals(AnnouncementNoticeStatus.IMMEDIATELY)) {
                    // TODO: 알림 보내는 함수 호출 필요.
                    announcement.setAnnouncementStatus(AnnouncementStatus.PUBLISHED);
                }
            }
        } else {
            // 공지사항이 알림 상태인데 notification도 그대로고 얘가 예약 상태일경우 -> 이때만 예약 시간 수정 필요.
            if (announcement.getAnnouncementStatus().equals(AnnouncementStatus.RESERVATION)
                && announcement.getAnnouncementNoticeStatus()
                .equals(AnnouncementNoticeStatus.RESERVATION)) {
                this.setAnnouncementReservationDate(announcement, request.getReservationDate());
            }
        }
        if (Objects.nonNull(request.getImage())) {
            this.objectService.deleteObject(announcement.getImage());
            String imageUrl = this.uploadAnnouncementImage(request.getImage());
            announcement.setImage(imageUrl);
        }
    }

    @Transactional
    public void setAnnouncementStatus(Long announcementId, AnnouncementStatus status) {
        Announcement announcement = this.getAnnouncement(announcementId);
        if (this.isHidePossible(announcement, status)) {
            throw new ConflictException("게재 상태가 아닌 공지사항을 숨김 처리 할 수 없습니다.");
        } else if (this.isShowPossible(announcement, status)) {
            throw new ConflictException("숨김 상태가 아닌 공지사항을 게재 처리 할 수 없습니다.");
        }
        announcement.setAnnouncementStatus(status);
    }

    private AnnouncementStatus getAnnouncementStatusFromAnnouncementNoticeStatus(
        AnnouncementNoticeStatus announcementNoticeStatus) {
        if (announcementNoticeStatus.equals(
            AnnouncementNoticeStatus.NONE) || announcementNoticeStatus.equals(
            AnnouncementNoticeStatus.IMMEDIATELY)) {
            return AnnouncementStatus.PUBLISHED;
        }
        return AnnouncementStatus.RESERVATION;
    }

    private String uploadAnnouncementImage(String imageData) {
        String announcementImageFolder = "announcement/";
        return this.objectService.uploadObject(announcementImageFolder + UUID.randomUUID(),
            imageData, "image");
    }

    private boolean isHidePossible(Announcement announcement, AnnouncementStatus status) {
        return status.equals(AnnouncementStatus.HIDING) && !announcement.getAnnouncementStatus()
            .equals(AnnouncementStatus.PUBLISHED);
    }

    private boolean isShowPossible(Announcement announcement, AnnouncementStatus status) {
        return status.equals(AnnouncementStatus.PUBLISHED) && !announcement.getAnnouncementStatus()
            .equals(AnnouncementStatus.HIDING);
    }

    private void setAnnouncementReservationDate(Announcement announcement, LocalDateTime date) {
        if (this.utilService.isBeforeThanNow(date)) {
            throw new ConflictException("예약 시간이 현재보다 이후여야 합니다!");
        }
        announcement.setReservationDate(date);
    }

    @Transactional
    public Page<Announcement> getAnnouncementListByStatusAndPageable(AnnouncementStatus status,
        Pageable pageable) {
        int page = pageable.getPageNumber() - 1;
        int size = pageable.getPageSize();
        Sort sort = Sort.by("id").ascending();

        Pageable reNew = PageRequest.of(page, size, sort);

        return announcementRepository.findAllByAnnouncementStatusOrderByIdDesc(status,
            reNew);
    }

    @Transactional
    public void increaseView(Announcement announcement) {
        announcement.setView(announcement.getView() + 1);
        this.announcementRepository.save(announcement);
    }
}
