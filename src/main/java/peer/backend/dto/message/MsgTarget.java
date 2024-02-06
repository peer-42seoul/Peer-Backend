package peer.backend.dto.message;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MsgTarget {
    private long userId;
    private String userNickname;
    private String userProfile;
    private boolean deleted;
};