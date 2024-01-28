package peer.backend.controller.dnd;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import peer.backend.dto.dndSub.CalendarEventDTO;
import peer.backend.dto.dndSub.DeleteTargetDTO;
import peer.backend.dto.dndSub.MemberDTO;
import peer.backend.entity.team.Team;
import peer.backend.entity.user.User;
import peer.backend.exception.BadRequestException;
import peer.backend.service.dnd.DnDSubService;


import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping(DnDSubController.WIDGET_URL)
public class DnDSubController {
    public static final String WIDGET_URL = "api/v1/dnd-sub";
    public static final String CALENDAR_IDENTIFIER = "calendar";
    private final DnDSubService dnDSubService;

    @PostMapping("calendar/team-list")
    @ApiOperation(value = "", notes = "달력을 위한 팀 멤버 리스트를 제공합니다. 임시용")
    public ResponseEntity<Object> getTeamMemberList(Authentication auth, @RequestBody long teamId) {
        Team target = this.dnDSubService.getTeamByTeamId(teamId);

        // redis 에 자주 쓸 가능성이 있는 team 정보 저장
        this.dnDSubService.saveTeamDataInRedis(Long.toString(teamId), CALENDAR_IDENTIFIER, target);

        // 유효성 검사
        if (this.dnDSubService.validCheckForTeam(target)
                || this.dnDSubService.validCheckUserWithTeam(target, User.authenticationToUser(auth))) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // 멤버 리스트 담기 & 에러 핸들링
        List<MemberDTO> ret;
        try {
           ret = this.dnDSubService.getMemberList(User.authenticationToUser(auth), target);
        }
        catch (NoSuchElementException | BadRequestException e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
        if(ret == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @PostMapping("calendar/set-alarm")
    @ApiOperation(value = "", notes = "알람으로 기록된 이벤트를 설정합니다.")
    public ResponseEntity<Object> setAlarmEvent(Authentication auth, @RequestBody CalendarEventDTO event) {
       // redis 에서 team 정보 찾기
        Team target = (Team)this.dnDSubService.getTeamDataInRedis(Long.toString(event.getTeamId()), CALENDAR_IDENTIFIER);
        if(target == null) {
            target = this.dnDSubService.getTeamByTeamId(event.getTeamId());
            this.dnDSubService.saveTeamDataInRedis(Long.toString(event.getTeamId()), CALENDAR_IDENTIFIER, target);
        }

        // 유효성 검사
        if (this.dnDSubService.validCheckForTeam(target)
                || this.dnDSubService.validCheckUserWithTeam(target, User.authenticationToUser(auth))) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Long eventId;

        try {
            eventId = this.dnDSubService.setEventToAlarm(event);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(eventId, HttpStatus.OK);
    }

    @DeleteMapping("calendar/delete-alarm")
    @ApiOperation(value = "", notes = "알람으로 기록된 이벤트를 삭제합니다.")
    public ResponseEntity<Object> deleteAlarmEvent(Authentication auth, @RequestBody DeleteTargetDTO event) {
        Team target = (Team)this.dnDSubService.getTeamDataInRedis(Long.toString(event.getTeamId()), CALENDAR_IDENTIFIER);
        if(target == null) {
            target = this.dnDSubService.getTeamByTeamId(event.getTeamId());
            this.dnDSubService.saveTeamDataInRedis(Long.toString(event.getTeamId()), CALENDAR_IDENTIFIER, target);
        }

        // 유효성 검사
        if (this.dnDSubService.validCheckForTeam(target)
                || this.dnDSubService.validCheckUserWithTeam(target, User.authenticationToUser(auth))) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        //대상 삭제
        try {
            this.dnDSubService.deleteEventFromAlarm(event);
        }catch (NoSuchElementException e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("calendar/update-alarm")
    @ApiOperation(value = "", notes = "알람으로 기록된 이벤트를 갱신합니다.")
    public ResponseEntity<Object> updateAlarmEvent(Authentication auth, @RequestBody CalendarEventDTO event) {
        // redis 에서 team 정보 찾기
        Team target = (Team)this.dnDSubService.getTeamDataInRedis(Long.toString(event.getTeamId()), CALENDAR_IDENTIFIER);
        if(target == null) {
            target = this.dnDSubService.getTeamByTeamId(event.getTeamId());
            this.dnDSubService.saveTeamDataInRedis(Long.toString(event.getTeamId()), CALENDAR_IDENTIFIER, target);
        }

        // 유효성 검사
        if (this.dnDSubService.validCheckForTeam(target)
                || this.dnDSubService.validCheckUserWithTeam(target, User.authenticationToUser(auth))) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Long eventId;

        try {
            eventId = this.dnDSubService.updateEventToAlarm(event);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }

        if (eventId == -1L){
            return new ResponseEntity<>(new NoSuchElementException("존재하지 않는 이벤트입니다."), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("calendar/test/total-alarm")
    @ApiOperation(value="", notes = "기록된 모든 알람을 호출합니다. 테스트용 코드입니다.")
    public ResponseEntity<Object> getTestTotalAlarmList(Authentication auth) {
        return new ResponseEntity<>(this.dnDSubService.getAllEvents(), HttpStatus.OK);
    }
}