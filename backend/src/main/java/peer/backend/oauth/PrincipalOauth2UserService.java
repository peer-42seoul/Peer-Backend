package peer.backend.oauth;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import peer.backend.entity.user.SocialLogin;
import peer.backend.entity.user.User;
import peer.backend.exception.ConflictException;
import peer.backend.exception.ForbiddenException;
import peer.backend.oauth.enums.LoginStatus;
import peer.backend.oauth.enums.SocialLoginProvider;
import peer.backend.oauth.provider.FortyTwoUserInfo;
import peer.backend.oauth.provider.GitHubUserInfo;
import peer.backend.oauth.provider.GoogleUserInfo;
import peer.backend.oauth.provider.OAuth2UserInfo;
import peer.backend.service.SocialLoginService;
import peer.backend.service.UserService;

@Slf4j
@RequiredArgsConstructor
@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;
    private final SocialLoginService socialLoginService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = this.getOAuth2UserInfo(oAuth2User, registrationId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginStatus loginStatus;
        User user = null;
        String socialEmail = oAuth2UserInfo.getEmail();
        SocialLogin socialInfo = this.socialLoginService.getSocialLogin(socialEmail);

        if (Objects.isNull(socialInfo)) {
            if (Objects.isNull(authentication)) {
                // 회원가입
                this.socialLoginService.putSocialLoginInRedis(new SocialLogin(user, oAuth2UserInfo,
                    userRequest.getAccessToken().getTokenValue(),
                    socialEmail));
                loginStatus = LoginStatus.REGISTER;
                user = User.builder().name("register").build();
            } else {
                // 연동
                loginStatus = LoginStatus.LINK;
                user = User.authenticationToUser(authentication);
                this.socialLoginService.save(new SocialLogin(user, oAuth2UserInfo,
                    userRequest.getAccessToken().getTokenValue(),
                    socialEmail));
            }
        } else {
            if (Objects.isNull(authentication)) {
                // 소셜 로그인
                loginStatus = LoginStatus.LOGIN;
                user = this.userService.findById(socialInfo.getUser().getId());
            } else {
                // 이중 연동
                throw new ConflictException("이미 연동되어있는 계정입니다!");
            }
        }

        return new PrincipalDetails(user, oAuth2User.getAttributes(), loginStatus, socialEmail);
    }

    private OAuth2UserInfo getOAuth2UserInfo(OAuth2User oAuth2User, String registrationId) {
        if (SocialLoginProvider.GOOGLE.getValue().equals(registrationId)) {
            return new GoogleUserInfo(oAuth2User.getAttributes());
        } else if (SocialLoginProvider.GITHUB.getValue().equals(registrationId)) {
            return new GitHubUserInfo(oAuth2User.getAttributes());
        } else if (SocialLoginProvider.FT.getValue().equals(registrationId)) {
            return new FortyTwoUserInfo(oAuth2User.getAttributes());
        } else {
            throw new ForbiddenException("지원하지 않는 OAuth 입니다!");
        }
    }
}
