package peer.backend.controller.noti;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.noti.SubscriptionDTO;
import peer.backend.service.noti.NotifcationPushService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(NotificationPushController.MAPPING_URL)
public class NotificationPushController {
    public static final String MAPPING_URL = "api/v1/noti-pwa";

    private final NotifcationPushService notificationPushService;

    @PutMapping("/spring/subscription")
    public ResponseEntity<Void> subscribeNotification(Authentication auth,
                                                      @RequestBody SubscriptionDTO data) {

        log.info(data.getDeviceInfo());
        return ResponseEntity.ok().build();
    }
}
