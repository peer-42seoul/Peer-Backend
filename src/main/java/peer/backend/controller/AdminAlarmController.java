package peer.backend.controller;

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
import peer.backend.dto.AlarmResponse;
import peer.backend.dto.adminAlarm.AlarmIdRequest;
import peer.backend.dto.adminAlarm.AlarmListResponse;
import peer.backend.dto.adminAlarm.SendAlarmRequest;
import peer.backend.dto.adminAlarm.UpdateAlarmRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/alarm")
public class AdminAlarmController {

    // TODO: alarm service dependency injection

    @PostMapping
    public ResponseEntity<Void> sendAlarm(@RequestBody @Valid SendAlarmRequest request) {
        // TODO: calling a function that send alarm
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<AlarmListResponse>> getReservedAlarmList(Pageable pageable) {
        // TODO: get a reserved alarm list and mapping to dto
        return ResponseEntity.ok().build();
    }

    @GetMapping("{alarmId")
    public ResponseEntity<AlarmResponse> getReservedAlarm(@PathVariable("alarmId") Long alarmId) {
        // TODO: get a reserved alarm and mapping to dto
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<Void> updateAlarm(@RequestBody @Valid UpdateAlarmRequest request) {
        // TODO: calling a function that update alarm
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAlarm(@RequestBody @Valid AlarmIdRequest request) {
        // TODO: calling a function that delete alarm
        return ResponseEntity.ok().build();
    }
}
