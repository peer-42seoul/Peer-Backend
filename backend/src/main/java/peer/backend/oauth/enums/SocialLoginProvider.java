package peer.backend.oauth.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import peer.backend.entity.team.enums.TeamMemberStatus;

@Getter
@RequiredArgsConstructor
public enum SocialLoginProvider {

    FT("ft"),
    GOOGLE("google"),
    GITHUB("github");

    private final String value;
}