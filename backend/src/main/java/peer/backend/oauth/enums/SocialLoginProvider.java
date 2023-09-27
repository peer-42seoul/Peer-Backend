package peer.backend.oauth.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SocialLoginProvider {

    FT("ft"),
    GOOGLE("google"),
    GITHUB("github");

    private final String value;
}