package peer.backend.controller.dnd;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import peer.backend.dto.dndSub.CalendarEventDTO;
import peer.backend.dto.dndSub.DeleteTargetDTO;
import peer.backend.service.dnd.DnDSubService;

@RestController
@RequiredArgsConstructor
@RequestMapping(DnDSubController.WIDGET_URL)
public class DnDSubController {
    public static final String WIDGET_URL = "api/v1/dnd-sub";
    private final DnDSubService dnDSubService;

    @GetMapping("calendar/team-list")
    @ApiOperation(value = "", notes = "달력을 위한 팀 멤버 리스트를 제공합니다.")
    public ResponseEntity<Object> getTeamMemberList(Authentication auth, @RequestBody long teamId) {
        // 인증 작업(팀 멤버 여부, 정상 팀 여부 등)
        // 팀 멤버 객체 생성
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("calendar/set-alarm")
    @ApiOperation(value = "", notes = "알람으로 기록된 이벤트를 설정합니다.")
    public ResponseEntity<Object> setAlarmEvent(Authentication auth, @RequestBody CalendarEventDTO event) {
        // 인증 작업(팀 멤버 여부, 정상 팀 여부 등)
        // 이벤트 파악 작업(중복 이벤트 여부, 이미 지난 이벤트 여부, 비정상 입력 여부 start-end 대비 team 일정 파악)
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("calendar/delete-alarm")
    @ApiOperation(value = "", notes = "알람으로 기록된 이벤트를 삭제합니다.")
    public ResponseEntity<Object> deleteAlarmEvent(Authentication auth, @RequestBody DeleteTargetDTO target) {
        // 인증 작업(팀 멤버 여부, 정상 팀 여부 등)
        // 삭제 처리
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("calendar/update-alarm")
    @ApiOperation(value = "", notes = "알람으로 기록된 이벤트를 갱신합니다.")
    public ResponseEntity<Object> updateAlarmEvent(Authentication auth, @RequestBody CalendarEventDTO event) {
        // 인증 작업(팀 멤버 여부, 정상 팀 여부 등)
        // 중복 여부 판단
        // 이벤트 파악 작업(중복 이벤트 여부, 이미 지난 이벤트 여부, 비정상 입력 여부 start-end 대비 team 일정 파악)
        // 갱신
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("calendar/test/total-alarm")
    @ApiOperation(value="", notes = "")
    public ResponseEntity<Object> getTestTotalAlarmList(Authentication auth) {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
