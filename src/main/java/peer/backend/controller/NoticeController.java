package peer.backend.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.notice.CreateNoticeRequest;
import peer.backend.dto.notice.NoticeIdRequest;
import peer.backend.dto.notice.NoticeListResponse;
import peer.backend.dto.notice.NoticeResponse;
import peer.backend.dto.notice.UpdateNoticeRequest;
import peer.backend.entity.notice.Notice;
import peer.backend.entity.notice.NoticeStatus;
import peer.backend.service.NoticeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/notice")
public class NoticeController {

    private final NoticeService noticeService;

    // TODO: 알림 enum이 예약일 경우 알림 예약 걸고(mayby spring batch?), 공지사항도 예약 걸어야됨.
    @PostMapping
    public void writeNotice(@RequestBody @Valid CreateNoticeRequest request) {
        this.noticeService.writeNotice(request);
    }

    @GetMapping
    public Page<NoticeListResponse> getNoticeList(Pageable pageable) {
        Page<Notice> noticeList = this.noticeService.getNoticeList(pageable);
        return noticeList.map(NoticeListResponse::new);
    }

    @GetMapping("{noticeId}")
    public NoticeResponse getNotice(@PathVariable("noticeId") Long noticeId) {
        Notice notice = this.noticeService.getNotice(noticeId);
        return new NoticeResponse(notice);
    }

    @DeleteMapping
    public void deleteNotice(@RequestBody @Valid NoticeIdRequest request) {
        this.noticeService.deleteNotice(request.getNoticeId());
    }

    // TODO: 지금은 단순 데이터를 수정만 하지만 알림이 추가되면 알림 여부에 따라 알림을 보낼지말지 추가되야함
    @PutMapping
    public void updateNotice(@RequestBody @Valid UpdateNoticeRequest request) {
        this.noticeService.updateNotice(request);
    }

    @PostMapping("hide")
    public void hideNotice(@RequestBody @Valid NoticeIdRequest request) {
        this.noticeService.setNoticeStatus(request.getNoticeId(), NoticeStatus.HIDING);
    }
}
