package peer.backend.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.notice.CreateNoticeRequest;
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
}
