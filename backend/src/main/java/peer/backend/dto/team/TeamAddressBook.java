package peer.backend.dto.team;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import peer.backend.entity.user.User;

@Getter
@RequiredArgsConstructor
public class TeamAddressBook {
    private final String name;
    private final String email;

    public TeamAddressBook (User User) {
        this.name = User.getNickname();
        this.email = User.getEmail();
    }
}
