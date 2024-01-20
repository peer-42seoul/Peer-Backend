package peer.backend.dto.privateInfo.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PrivateActions {
    SIGNUP(662, "SIGNUP"),
    PASSWORDCHECK(1425, "PASSWORDCHECK"),
    PASSWORDMODIFY(1563, "PASSWORDMODIFY");

    private final int code;
    private final String description;
}
