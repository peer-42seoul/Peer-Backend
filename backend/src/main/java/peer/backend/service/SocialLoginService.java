package peer.backend.service;

import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import peer.backend.annotation.tracking.UserFtLinkTracking;
import peer.backend.entity.user.SocialLogin;
import peer.backend.entity.user.User;
import peer.backend.oauth.enums.SocialLoginProvider;
import peer.backend.oauth.provider.FortyTwoUserInfo;
import peer.backend.oauth.provider.OAuth2UserInfo;
import peer.backend.repository.user.SocialLoginRepository;

@RequiredArgsConstructor
@Service
public class SocialLoginService {

    private final SocialLoginRepository socialLoginRepository;

    @Transactional
    public SocialLogin getSocialLogin(String email) {
        return this.socialLoginRepository.findByEmail(email).orElse(null);
    }

    @Transactional
    @UserFtLinkTracking
    public SocialLogin save(User user, OAuth2UserInfo oAuth2UserInfo, String accessToken,
        String email) {
        SocialLogin socialLogin = SocialLogin.builder()
            .user(user)
            .provider(oAuth2UserInfo.getProvider())
            .providerId(oAuth2UserInfo.getProviderId())
            .accessToken(accessToken)
            .email(email)
            .build();
        if (socialLogin.getProvider() == SocialLoginProvider.FT) {
            socialLogin.setIntraId(((FortyTwoUserInfo) oAuth2UserInfo).getIntraId());
        }
        return this.socialLoginRepository.save(socialLogin);
    }
}
