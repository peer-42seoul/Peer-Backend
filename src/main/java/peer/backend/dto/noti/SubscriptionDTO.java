package peer.backend.dto.noti;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SubscriptionDTO {
    private String firebaseToken;
    private String deviceInfo;
}
