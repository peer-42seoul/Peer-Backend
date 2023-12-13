package peer.backend.dto.blacklist;


import lombok.Getter;
import peer.backend.entity.blacklist.Blacklist;
import peer.backend.entity.blacklist.BlacklistType;
import peer.backend.entity.user.User;

@Getter
public class BlacklistResponse {

    private final Long blacklistId;
    private final Long userId;
    private final String nickname;
    private final String name;
    private final String email;
    private final String content;
    private final BlacklistType type;

    public BlacklistResponse(Blacklist blacklist) {
        User user = blacklist.getUser();

        this.blacklistId = blacklist.getId();
        this.userId = user.getId();
        this.nickname = user.getNickname();
        this.name = user.getName();
        this.email = user.getEmail();
        this.content = blacklist.getContent();
        this.type = blacklist.getType();
    }
}
