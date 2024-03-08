package peer.backend.dto.noti.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeviceType {
    MOBILE_I("iOS"),
    MOBILE_A("android"),
    PC_I("Mac"),
    PC_W("Windows"),
    PC_O("Others");

    private final String value;
}
