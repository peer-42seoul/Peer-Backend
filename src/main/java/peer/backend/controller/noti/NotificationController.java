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
import peer.backend.dto.noti.NotificationDTO;
import peer.backend.dto.noti.enums.NotificationType;
import peer.backend.dto.user.UserAlarmSettingDTO;
import peer.backend.entity.user.User;
import peer.backend.exception.BadRequestException;
import peer.backend.repository.user.UserRepository;
import peer.backend.service.noti.NotificationMainService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(NotificationController.MAPPING_URL)
@Slf4j
public class NotificationController {
    public static final String MAPPING_URL = "api/v1/noti";
    private final NotificationMainService notificationMainService;

    private final UserRepository userRepository;

    ///api/v1/noti/spring?type=${}&pgIdx=${number}&pgSize={number}
    @GetMapping("/spring")
    public ResponseEntity<?> getAlarmList(Authentication auth,
                                          @Param("type") NotificationType type,
                                          @Param("pgIdx") Long pgIdx,
                                          @Param("Size") Long size) {
        List<NotificationDTO> result = new ArrayList<>();
        try {
            result = this.notificationMainService.getNotificationList(User.authenticationToUser(auth), type, pgIdx, size);
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(result);
    }

    @DeleteMapping("/spring/delete-all")
    public ResponseEntity<Void> deleteAlarmAll(Authentication auth,
                                            @Param("type") NotificationType type) {
        this.notificationMainService.deleteNotificationAll(
                User.authenticationToUser(auth),
                type);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/spring/delete-target")
    public ResponseEntity<?> deleteAlarmTarget(Authentication auth,
                                               @Param("notificationId") Long notificationId) {
        try {
            this.notificationMainService.deleteNotification(
                    User.authenticationToUser(auth),
                    notificationId
            );
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e);
        }
        return ResponseEntity.ok().build();
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

    @GetMapping("/summary")
    public ResponseEntity<?> summarizeYourAlarm(Authentication auth) {
        User user = this.userRepository.findById(User.authenticationToUser(auth).getId()).orElseThrow(() -> new BadRequestException("잘못된 요청입니다."));
        Integer newAlarm = user.getNewAlarmCounter();
        if(newAlarm.equals(0))
            return ResponseEntity.noContent().build();
        else
            return ResponseEntity.ok(newAlarm);
    }
}
