package peer.backend.oauth.provider;

import java.util.Map;
import peer.backend.oauth.enums.SocialLoginProvider;

public class FortyTwoUserInfo implements OAuth2UserInfo {

    private Map<String, Object> attributes; // oauth2User.getAttributes();

    public FortyTwoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return String.valueOf(this.attributes.get("id"));
    }

    @Override
    public SocialLoginProvider getProvider() {
        return SocialLoginProvider.FT;
    }

    @Override
    public String getEmail() {
        return (String) this.attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) this.attributes.get("name");
    }

    public String getIntraId() {
        return (String) this.attributes.get("login");
    }
}
