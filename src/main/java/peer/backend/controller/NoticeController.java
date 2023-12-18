package peer.backend.controller;


import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.alarm.AlarmDto;
import peer.backend.entity.alarm.Alarm;
import peer.backend.entity.user.User;
import peer.backend.service.alarm.AlarmAdminService;
import peer.backend.service.alarm.AlarmService;

@RequiredArgsConstructor
@RestController
@Slf4j
public class NoticeController {
    private final AlarmService alarmService;
    private final AlarmAdminService alarmAdminService;

    @PutMapping("/api/v1/admin/noti/save")
    public ResponseEntity<Object> saveAlarmAdmin(@RequestBody AlarmDto dto) {
        Alarm alarm = alarmService.saveAlarm(dto);

        return ResponseEntity.ok().build();
    }
    @GetMapping("/api/v1/admin/noti/spring")
    public ResponseEntity<Object> getAlarm(@RequestParam(value = "type") String type) {
        List<Alarm> alarms = alarmAdminService.getAlarm(type);
        return ResponseEntity.ok().body(alarms);
    }

    @PutMapping("/api/v1/noti/save")
    public ResponseEntity<Object> saveAlarm(@RequestBody AlarmDto dto) {
        Alarm alarm = alarmService.saveAlarm(dto);
        alarmService.saveAlarmTarget(alarm);

        return ResponseEntity.ok().build();
    }
    @GetMapping("/api/v1/noti/general")
    public ResponseEntity<Object> getAlarmGeneral(Authentication authentication) {
        User user = User.authenticationToUser(authentication);
        List<Alarm> alarms = alarmService.getAlarmGeneral(user.getId());
        return ResponseEntity.ok().body(alarms);
    }

    @DeleteMapping("spring/delete/all")
    public ResponseEntity<Object> deleteAllAlarm(@RequestParam(value = "type") String type) {
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("")
    public ResponseEntity<Object> deleteAlarm(@RequestParam Long id) {
        alarmService.deleteAlarm(id);
        return ResponseEntity.ok().build();
    }
}
