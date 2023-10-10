package peer.backend.dto.profile.information;

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
