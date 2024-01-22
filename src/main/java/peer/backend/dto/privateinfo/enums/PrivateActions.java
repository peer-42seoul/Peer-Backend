package peer.backend.dto.privateinfo.enums;

import lombok.Getter;

@Getter
public enum PrivateActions {
    SIGNUP(662, "SIGNUP"),
    PASSWORDCHECK(1425, "PASSWORDCHECK"),
    PASSWORDMODIFY(1563, "PASSWORDMODIFY");

    private final int code;
    private final String description;
    PrivateActions(int value, String description) {
        this.code = value;
        this.description = description;
    }
}
