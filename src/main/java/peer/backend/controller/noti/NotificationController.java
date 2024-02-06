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

@RestController
@RequiredArgsConstructor
@RequestMapping(NotificationController.MAPPING_URL)
@Slf4j
public class NotificationController {
    public static final String MAPPING_URL = "/api/v1/noti/spring";

    ///api/v1/noti/spring?type=${}&pgIdx=${number}&pgSize={number}
    @GetMapping()
    public ResponseEntity<?> getAlarmList(Authentication auth,
                                          @Param("type") NotificationType type,
                                          @Param("pgIdx") Long pageIndex,
                                          @Param("Size") Long size) {
        //TODO : making code logic
        return new ResponseEntity<>(HttpStatus.OK);
    }

    ///api/v1/noti/spring/delete-all?type=${}
    @DeleteMapping("/delete-all")
    public ResponseEntity<?> deleteAlarmAll(Authentication auth,
                                            @Param("type") NotificationType type) {
        //TODO : making code logic
        return new ResponseEntity<>(HttpStatus.OK);
    }

    ///api/v1/noti/spring/delete-taget?targetId=${alarmId}
    @DeleteMapping("delete-target")
    public ResponseEntity<?> deleteAlarmTarget(Authentication auth,
                                               @Param("targetId") Long alarmId) {
        //TODO : making code logic
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
