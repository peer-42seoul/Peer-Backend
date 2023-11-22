package peer.backend.dto.profile.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PersonalInfoResponse {
    private String name;
    private String email;
    private String local;
    private String authenticationFt;
    private String authenticationGoogle;
}
