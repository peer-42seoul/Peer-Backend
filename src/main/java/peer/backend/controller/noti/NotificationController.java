package peer.backend.controller.noti;

import io.lettuce.core.dynamic.annotation.Param;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.noti.enums.NotificationType;
import peer.backend.dto.user.UserAlarmSettingDTO;
import peer.backend.entity.user.User;
import peer.backend.service.noti.NotificationMainService;

@RestController
@RequiredArgsConstructor
@RequestMapping(NotificationController.MAPPING_URL)
@Slf4j
public class NotificationController {
    public static final String MAPPING_URL = "api/v1/noti";
    public final NotificationMainService notificationMainService;

    ///api/v1/noti/spring?type=${}&pgIdx=${number}&pgSize={number}
    @GetMapping("/spring")
    public ResponseEntity<?> getAlarmList(Authentication auth,
                                          @Param("type") NotificationType type,
                                          @Param("pgIdx") Long pageIndex,
                                          @Param("Size") Long size) {
        //TODO : making code logic
        return new ResponseEntity<>(HttpStatus.OK);
    }

    ///api/v1/noti/spring/delete-all?type=${}
    @DeleteMapping("/spring/delete-all")
    public ResponseEntity<?> deleteAlarmAll(Authentication auth,
                                            @Param("type") NotificationType type) {
        //TODO : making code logic
        return new ResponseEntity<>(HttpStatus.OK);
    }

    ///api/v1/noti/spring/delete-taget?targetId=${alarmId}
    @DeleteMapping("/spring/delete-target")
    public ResponseEntity<?> deleteAlarmTarget(Authentication auth,
                                               @Param("targetId") Long alarmId) {
        //TODO : making code logic
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping("/get-my-alarm")
    public ResponseEntity<UserAlarmSettingDTO> getUserAlarmSettings(Authentication auth) {
        User user = User.authenticationToUser(auth);
        UserAlarmSettingDTO result = UserAlarmSettingDTO.builder()
                .keyword(user.isKeywordRecommendAlarm())
                .team(user.isTeamAlarm())
                .message(user.isMessageAlarm())
                .nightAlarm(user.isNightAlarm())
                .build();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/alarm-setting")
    public ResponseEntity<Void> setUserAlarmSettings(Authentication auth,
                                                     @Param("alarmType") String type,
                                                     @Param("value") boolean value) {
        try {
            this.notificationMainService.setAlarmForUser(User.authenticationToUser(auth),
                    type,
                    value);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
