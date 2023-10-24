package peer.backend.dto.message;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MsgOwner {
    private long userId;
    private String userNickname;
    private String userProfile;
};