package peer.backend.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.alarm.AlarmDto;
import peer.backend.entity.alarm.Alarm;
import peer.backend.service.alarm.AlarmService;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/test/alarm")
public class TestController {
    private final AlarmService alarmService;

    @PostMapping("test")
    public ResponseEntity<Object> test(@RequestBody AlarmDto dto) {
        Alarm alarm = alarmService.AlarmFromDto(dto);
        alarmService.saveAlarm(alarm);
        return ResponseEntity.ok().build();
    }
}
