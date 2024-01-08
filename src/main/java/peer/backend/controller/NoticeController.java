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
import peer.backend.entity.noti.Notification;
import peer.backend.entity.user.User;
import peer.backend.service.noti.NotificationAdminService;
import peer.backend.service.noti.NotificationService;

@RequiredArgsConstructor
@RestController
@Slf4j
public class NoticeController {
    private final NotificationService notificationService;
    private final NotificationAdminService notificationAdminService;

    @PutMapping("/api/v1/admin/noti/save")
    public ResponseEntity<Object> saveAlarmAdmin(@RequestBody AlarmDto dto) {
        Notification alarm = notificationService.saveAlarm(dto);

        return ResponseEntity.ok().build();
    }
    @GetMapping("/api/v1/admin/noti/spring")
    public ResponseEntity<Object> getAlarm(@RequestParam(value = "type") String type) {
        List<Notification> alarms = notificationAdminService.getAlarm(type);
        return ResponseEntity.ok().body(alarms);
    }

    @PutMapping("/api/v1/noti/save")
    public ResponseEntity<Object> saveAlarm(@RequestBody AlarmDto dto) {
        Notification alarm = notificationService.saveAlarm(dto);
        notificationService.saveAlarmTarget(alarm);

        return ResponseEntity.ok().build();
    }
    @GetMapping("/api/v1/noti/general")
    public ResponseEntity<Object> getAlarmGeneral(Authentication authentication) {
        User user = User.authenticationToUser(authentication);
        List<Notification> alarms = notificationService.getAlarmGeneral(user.getId());
        return ResponseEntity.ok().body(alarms);
    }

    @DeleteMapping("spring/delete/all")
    public ResponseEntity<Object> deleteAllAlarm(@RequestParam(value = "type") String type) {
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("")
    public ResponseEntity<Object> deleteAlarm(@RequestParam Long id) {
        notificationService.deleteAlarm(id);
        return ResponseEntity.ok().build();
    }
}
