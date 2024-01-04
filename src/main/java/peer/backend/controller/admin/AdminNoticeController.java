package peer.backend.controller.admin;

import java.awt.print.Pageable;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.NoticeResponse;
import peer.backend.dto.admin.notice.NoticeIdRequest;
import peer.backend.dto.admin.notice.NoticeListResponse;
import peer.backend.dto.admin.notice.SendNoticeRequest;
import peer.backend.dto.admin.notice.UpdateNoticeRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/notice")
public class AdminNoticeController {

    // TODO: notice service dependency injection

    @PostMapping
    public ResponseEntity<Void> sendNotice(@RequestBody @Valid SendNoticeRequest request) {
        // TODO: calling a function that send notice
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<NoticeListResponse>> getReservedNoticeList(Pageable pageable) {
        // TODO: get a reserved notice list and mapping to dto
        return ResponseEntity.ok().build();
    }

    @GetMapping("{noticeId")
    public ResponseEntity<NoticeResponse> getReservedNotice(
        @PathVariable("noticeId") Long noticeId) {
        // TODO: get a reserved notice and mapping to dto
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<Void> updateNotice(@RequestBody @Valid UpdateNoticeRequest request) {
        // TODO: calling a function that update notice
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteNotice(@RequestBody @Valid NoticeIdRequest request) {
        // TODO: calling a function that delete notice
        return ResponseEntity.ok().build();
    }
}
