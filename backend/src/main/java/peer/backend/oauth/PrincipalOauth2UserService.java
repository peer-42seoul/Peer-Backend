package peer.backend.oauth;

import java.time.LocalDate;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import peer.backend.entity.user.User;
import peer.backend.exception.BadRequestException;
import peer.backend.exception.ForbiddenException;
import peer.backend.exception.NotFoundException;
import peer.backend.oauth.provider.FortyTwoUserInfo;
import peer.backend.oauth.provider.GitHubUserInfo;
import peer.backend.oauth.provider.GoogleUserInfo;
import peer.backend.oauth.provider.OAuth2UserInfo;
import peer.backend.repository.user.UserRepository;

@Service
@Slf4j
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

  private static final String GOOGLE = "google";
  private static final String FT = "ft";
  private static final String GITHUB = "github";


  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Autowired
  private UserRepository userRepository;

  // 구글로부터 받은 userRequest 데이터에 대한 후처리 되는 함수
  // 함수 종료 시 @AuthenticationPrincipal 어노테이션이 붙은 객체가 만들어진다.
  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    System.out.println("getClientRegistration: " + userRequest.getClientRegistration());
    System.out.println("getAccessToken: " + userRequest.getAccessToken().getTokenValue());

    OAuth2User oAuth2User = super.loadUser(userRequest);

    // 구글 로그인 버튼 클릭 -> 구글 로그인창 -> 로그인을 완료 -> code를 리턴(OAuth-Client 라이브러리) -> AccessToken 요청
    // userRequest 정보 -> loadUser 함수 호출 -> 구글로부터 회원 프로필을 받음
    System.out.println("getAttributes: " + oAuth2User.getAttributes());

    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    OAuth2UserInfo oAuth2UserInfo = this.getOAuth2UserInfo(oAuth2User, registrationId);

    String provider = oAuth2UserInfo.getProvider();
    String providerId = oAuth2UserInfo.getProviderId();
    String username = provider + "_" + providerId;
    String password = bCryptPasswordEncoder.encode("겟인데어");
    String email = oAuth2UserInfo.getEmail();
    String role = "ROLE_USER";

    User user = userRepository.findByEmail(email).orElse(null);

    boolean isRegistered = true;

    if (user == null) {
      user = User.builder().name("tmp").build();
      isRegistered = false;
//      userRepository.save(user);
    }
    return new PrincipalDetails(user, oAuth2User.getAttributes(), isRegistered);
  }

  private OAuth2UserInfo getOAuth2UserInfo(OAuth2User oAuth2User, String registrationId) {
    OAuth2UserInfo oAuth2UserInfo;
      switch (registrationId) {
          case "google":
              return new GoogleUserInfo(oAuth2User.getAttributes());
          case "github":
              return new GitHubUserInfo(oAuth2User.getAttributes());
          case "ft":
              return new FortyTwoUserInfo(oAuth2User.getAttributes());
          default:
            throw new ForbiddenException("지원하지 않는 OAuth 입니다.");
      }

  }
}
