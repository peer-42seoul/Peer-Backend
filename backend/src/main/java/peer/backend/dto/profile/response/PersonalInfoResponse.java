package peer.backend.dto.profile.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PersonalInfoResponse {
    private String name;
    private String email;
    private String local;
    private String authentication;
}
