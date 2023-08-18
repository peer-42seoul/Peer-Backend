package peer.backend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.entity.user.User;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileRequest {
    private User user;
}
