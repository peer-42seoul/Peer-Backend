package peer.backend.oauth;

import java.util.List;
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
import peer.backend.service.blacklist.BlacklistService;

@Slf4j
@RequiredArgsConstructor
@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;
    private final SocialLoginService socialLoginService;
    private final BlacklistService blacklistService;

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
                user = User.builder().email("register").build();
            } else {
                // 연동
                loginStatus = LoginStatus.LINK;
                user = User.authenticationToUser(authentication);
                if (this.alreadyLinkCheck(user, oAuth2UserInfo)) {
                    throw new ConflictException("해당 소설 서비스로 이미 연동이 되어 있습니다!");
                }
                this.socialLoginService.save(new SocialLogin(user, oAuth2UserInfo,
                    userRequest.getAccessToken().getTokenValue(),
                    socialEmail));
            }
        } else {
            if (Objects.isNull(authentication)) {
                // 소셜 로그인
                loginStatus = LoginStatus.LOGIN;
                user = this.userService.findById(socialInfo.getUser().getId());

                if (this.blacklistService.isExistsByUserId(user.getId())) {
                    throw new ForbiddenException("정지된 계정입니다.");
                }
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

    private boolean alreadyLinkCheck(User user, OAuth2UserInfo oAuth2UserInfo) {
        List<SocialLogin> socialLoginList = this.socialLoginService.getSocialLoginListByUserId(
            user.getId());

        for (SocialLogin socialLogin : socialLoginList) {
            if (oAuth2UserInfo.getProvider() == socialLogin.getProvider()) {
                return true;
            }
        }

        return false;
    }
}
