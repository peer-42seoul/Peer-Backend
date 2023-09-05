package peer.backend.entity.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageType {

    SEND("송신"),
    RECEIVE("수신");
    private final String type;
}
