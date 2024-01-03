package peer.backend.controller;

import java.awt.print.Pageable;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.adminAlarm.AlarmListResponse;
import peer.backend.dto.adminAlarm.SendAlarmRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/alarm")
public class AdminAlarmController {

    // TODO: alarm service dependency injection

    @PostMapping
    public ResponseEntity<Void> sendAlarm(@RequestBody @Valid SendAlarmRequest request) {
        // TODO: calling a function that send notification
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<AlarmListResponse>> getReservedAlarmList(Pageable pageable) {
        // TODO: get a reserved alarm list and mapping to dto
        return ResponseEntity.ok().build();
    }

}
