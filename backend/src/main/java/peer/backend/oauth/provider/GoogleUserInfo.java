package peer.backend.oauth.provider;

import java.util.Map;
import peer.backend.oauth.enums.SocialLoginProvider;

public class GoogleUserInfo implements OAuth2UserInfo {

  private Map<String, Object> attributes; // oauth2User.getAttributes();

  public GoogleUserInfo(Map<String, Object> attributes) {
    this.attributes = attributes;
  }

  @Override
  public String getProviderId() {
    return (String) this.attributes.get("sub");
  }

  @Override
  public SocialLoginProvider getProvider() {
    return SocialLoginProvider.GOOGLE;
  }

  @Override
  public String getEmail() {
    return (String) this.attributes.get("email");
  }

  @Override
  public String getName() {
    return (String) this.attributes.get("name");
  }
}
