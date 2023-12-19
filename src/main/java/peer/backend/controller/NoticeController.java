package peer.backend.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.notice.CreateNoticeRequest;
import peer.backend.dto.notice.NoticeResponse;
import peer.backend.entity.notice.Notice;
import peer.backend.service.NoticeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/notice")
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping
    public void writeNotice(@RequestBody @Valid CreateNoticeRequest request) {
        this.noticeService.writeNotice(request);
    }

    @GetMapping
    public Page<NoticeResponse> getNoticeList(Pageable pageable) {
        Page<Notice> noticeList = this.noticeService.getNoticeList(pageable);
        return noticeList.map(NoticeResponse::new);
    }
}
