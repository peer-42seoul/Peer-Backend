package peer.backend.dto.user;

import lombok.Getter;
import peer.backend.dto.AlarmResponseContainable;
import peer.backend.entity.user.User;

@Getter
public class UserDefaultResponse implements AlarmResponseContainable {

    private final Long userId;
    private final String nickname;
    private final String email;
    private final String name;

    public UserDefaultResponse(User user) {
        this.userId = user.getId();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.name = user.getName();
    }
}
