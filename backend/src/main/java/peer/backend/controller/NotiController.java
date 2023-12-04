package peer.backend.controller;


import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.alarm.AlarmDto;
import peer.backend.entity.alarm.Alarm;
import peer.backend.service.alarm.AlarmService;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/noti")
public class NotiController {
    private final AlarmService alarmService;

    @PostMapping("")
    public ResponseEntity<Object> saveAlarm(@RequestBody AlarmDto dto) {
        Alarm alarm = alarmService.AlarmFromDto(dto);
        alarmService.saveAlarm(alarm);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{target}")
    public ResponseEntity<Object> saveAlarm(@RequestBody AlarmDto dto, @PathVariable Long target) {
        Alarm alarm = alarmService.AlarmFromDto(dto, target);
        alarmService.saveAlarm(alarm);
        return ResponseEntity.ok().build();
    }

    @GetMapping("")
    public ResponseEntity<Object> getAlarm(@RequestParam Long target) {
        List<Alarm> alarms = alarmService.getAlarm(target);
        return ResponseEntity.ok().body(alarms);
    }

    @GetMapping("/general")
    public ResponseEntity<Object> getAlarmGeneral() {
        List<Alarm> alarms = alarmService.getAlarmGeneral();
        return ResponseEntity.ok().body(alarms);
    }

    @DeleteMapping("")
    public ResponseEntity<Object> deleteAlarm(@RequestParam Long id) {
        alarmService.deleteAlarm(id);
        return ResponseEntity.ok().build();
    }
}
