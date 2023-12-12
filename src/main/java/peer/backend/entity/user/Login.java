package peer.backend.entity.user;

import peer.backend.entity.user.enums.Role;

public interface Login {

    Long getId();

    String getEmail();

    String getPassword();

    Role getRole();
}
