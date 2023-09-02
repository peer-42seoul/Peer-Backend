package peer.backend.entity.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageType {

    RECEIVE("수신"),
    SEND("송신")
    ;
    private final String type;
}
