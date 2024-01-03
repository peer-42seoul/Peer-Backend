package peer.backend.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
