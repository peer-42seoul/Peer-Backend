package peer.backend.dto.noti.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 알림의 대상이 누구인지를 지정해준다. 첫번째 인자로 제공되는 값 T에 Team 객체를 넣을 것인지 User 객체를 넣을 건지를 결정한다.
 */
@Getter
@RequiredArgsConstructor
public enum NotificationTargetType {
    TEAM("TEAM"),
    USER("USER");

    private final String notificationTargetType;
}
