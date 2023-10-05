package peer.backend.oauth.provider;

import peer.backend.oauth.enums.SocialLoginProvider;

public interface OAuth2UserInfo {

  String getProviderId();

  SocialLoginProvider getProvider();

  String getEmail();

  String getName();
}
