package peer.backend.oauth.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoginStatus {
    LOGIN("login"),
    LINK("link"),
    REGISTER("register"),
    BLOCKED("blocked"),
    ALREADY_LINK("already_link"),
    DUPLICATE("duplicate");

    private final String value;
}
