package peer.backend.oauth.provider;

import java.util.Map;

public class GitHubUserInfo implements OAuth2UserInfo {

  private Map<String, Object> attributes; // oauth2User.getAttributes();

  public GitHubUserInfo(Map<String, Object> attributes) {
    this.attributes = attributes;
  }

  @Override
  public String getProviderId() {
    return String.valueOf(this.attributes.get("id"));
  }

  @Override
  public String getProvider() {
    return "github";
  }

  @Override
  public String getEmail() {
    return (String) this.attributes.get("email");
  }

  @Override
  public String getName() {
    return (String) this.attributes.get("displayname");
  }
}
