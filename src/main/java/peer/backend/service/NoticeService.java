package peer.backend.service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import peer.backend.dto.notice.CreateNoticeRequest;
import peer.backend.dto.notice.UpdateNoticeRequest;
import peer.backend.entity.banner.Banner;
import peer.backend.entity.notice.Notice;
import peer.backend.entity.notice.NoticeStatus;
import peer.backend.entity.notice.Notification;
import peer.backend.exception.ConflictException;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.notice.NoticeRepository;
import peer.backend.service.file.ObjectService;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final ObjectService objectService;
    private final UtilService utilService;

    @Transactional
    public void writeNotice(CreateNoticeRequest request) {
        Notice notice = this.createNoticeFromCreateNoticeRequest(request);
        this.noticeRepository.save(notice);
    }

    @Transactional
    public Page<Notice> getNoticeList(Pageable pageable) {
        return this.noticeRepository.findAll(pageable);
    }

    @Transactional
    public Notice getNotice(Long noticeId) {
        return this.noticeRepository.findById(noticeId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 공지사항 Id 입니다."));
    }

    @Transactional
    public void deleteNotice(Long noticeId) {
        Notice notice = this.getNotice(noticeId);
        this.noticeRepository.deleteById(noticeId);
        this.objectService.deleteObject(notice.getImage());
    }

    @Transactional
    public void updateNotice(UpdateNoticeRequest request) {
        Notice notice = this.noticeRepository.findById(request.getNoticeId())
            .orElseThrow(() -> new NotFoundException("존재하지 않는 공지사항 ID 입니다."));
        this.updateNoticeFromRequest(notice, request);
    }

    private Notice createNoticeFromCreateNoticeRequest(CreateNoticeRequest request) {
        String imageUrl = this.uploadNoticeImage(request.getImage());

        Notice notice = Notice.builder()
            .title(request.getTitle())
            .writer(request.getWriter())
            .content(request.getContent())
            .status(this.getNoticeStatusFromNotification(request.getNotification()))
            .notification(request.getNotification())
            .image(imageUrl)
            .view(0L)
            .build();

        if (request.getNotification().equals(Notification.RESERVATION) && Objects.nonNull(
            request.getReservationDate())) {
            this.setNoticeReservationDate(notice, request.getReservationDate());
        }

        return notice;
    }

    private NoticeStatus getNoticeStatusFromNotification(Notification notification) {
        if (notification.equals(Notification.NONE) || notification.equals(
            Notification.IMMEDIATELY)) {
            return NoticeStatus.PUBLISHED;
        }
        return NoticeStatus.RESERVATION;
    }

    @Transactional
    public void updateNoticeFromRequest(Notice notice, UpdateNoticeRequest request) {
        notice.setTitle(request.getTitle());
        notice.setWriter(request.getWriter());
        notice.setContent(request.getContent());
        // notification이 수정됐다!!
        if (!notice.getNotification().equals(request.getNotification())) {
            // 공지사항이 게재 or 숨김 상태일 경우
            if (notice.getStatus().equals(NoticeStatus.PUBLISHED) || notice.getStatus()
                .equals(NoticeStatus.HIDING)) {
                throw new ConflictException("공지사항이 게재거나 숨김 상태일때는 알림 여부를 변경할 수 없습니다.");
                // 공지사항이 예약 상태일 경우
            } else {
                // 알림 여부 없음으로 변경
                if (request.getNotification().equals(Notification.NONE)) {
                    notice.setStatus(NoticeStatus.PUBLISHED);
                    // 알림 여부 즉시로 변경
                } else if (request.getNotification().equals(Notification.IMMEDIATELY)) {
                    // TODO: 알림 보내는 함수 호출 필요.
                    notice.setStatus(NoticeStatus.PUBLISHED);
                }
            }
        } else {
            // 공지사항이 알림 상태인데 notification도 그대로고 얘가 예약 상태일경우 -> 이때만 예약 시간 수정 필요.
            if (notice.getStatus().equals(NoticeStatus.RESERVATION) && notice.getNotification()
                .equals(Notification.RESERVATION)) {
                this.setNoticeReservationDate(notice, request.getReservationDate());
            }
        }
        if (Objects.nonNull(request.getImage())) {
            this.objectService.deleteObject(notice.getImage());
            String imageUrl = this.uploadNoticeImage(request.getImage());
            notice.setImage(imageUrl);
        }
    }

    @Transactional
    public void setNoticeStatus(Long noticeId, NoticeStatus status) {
        Notice notice = this.getNotice(noticeId);
        if (this.isHidePossible(notice, status)) {
            throw new ConflictException("게재 상태가 아닌 공지사항을 숨김 처리 할 수 없습니다.");
        } else if (this.isShowPossible(notice, status)) {
            throw new ConflictException("숨김 상태가 아닌 공지사항을 게재 처리 할 수 없습니다.");
        }
        notice.setStatus(status);
    }

    private String uploadNoticeImage(String imageData) {
        String noticeImageFolder = "notice/";
        return this.objectService.uploadObject(noticeImageFolder + UUID.randomUUID(),
            imageData, "image");
    }

    private boolean isHidePossible(Notice notice, NoticeStatus status) {
        return status.equals(NoticeStatus.HIDING) && !notice.getStatus()
            .equals(NoticeStatus.PUBLISHED);
    }

    private boolean isShowPossible(Notice notice, NoticeStatus status) {
        return status.equals(NoticeStatus.PUBLISHED) && !notice.getStatus()
            .equals(NoticeStatus.HIDING);
    }

    private void setNoticeReservationDate(Notice notice, LocalDateTime date) {
        if (!this.utilService.checkDatePastNow(date)) {
            throw new ConflictException("예약 시간이 현재보다 이후여야 합니다!");
        }
        notice.setReservationDate(date);
    }
}
