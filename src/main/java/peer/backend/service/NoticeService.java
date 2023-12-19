package peer.backend.service;

import java.util.UUID;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import peer.backend.dto.notice.CreateNoticeRequest;
import peer.backend.entity.notice.Notice;
import peer.backend.entity.notice.NoticeStatus;
import peer.backend.entity.notice.Notification;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.notice.NoticeRepository;
import peer.backend.service.file.ObjectService;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final ObjectService objectService;

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

    private Notice createNoticeFromCreateNoticeRequest(CreateNoticeRequest request) {
        String imageUrl = this.objectService.uploadObject("notice/" + UUID.randomUUID(),
            request.getImage(), "image");

        return Notice.builder()
            .title(request.getTitle())
            .writer(request.getWriter())
            .content(request.getContent())
            .status(this.getNoticeStatusFromNotification(request.getNotification()))
            .notification(request.getNotification())
            .reservation_date(request.getReservationDate())
            .image(imageUrl)
            .view(0L)
            .build();
    }

    private NoticeStatus getNoticeStatusFromNotification(Notification notification) {
        if (notification.equals(Notification.NONE) || notification.equals(
            Notification.IMMEDIATELY)) {
            return NoticeStatus.PUBLISHED;
        }
        return NoticeStatus.RESERVATION;
    }
}
