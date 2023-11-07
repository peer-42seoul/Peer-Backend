package peer.backend.oauth;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import peer.backend.config.jwt.TokenProvider;
import peer.backend.entity.user.User;
import peer.backend.oauth.enums.LoginStatus;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuthAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${url.front-server-url}")
    private String REDIRECT_URL;


    private final TokenProvider tokenProvider;

    @Value("${jwt.token.validity-in-seconds-refresh}")
    private long refreshExpirationTime;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
//        User user = principalDetails.getUser();
        User user = User.authenticationToUser(authentication);
        LoginStatus loginStatus = principalDetails.getLoginStatus();
        String redirectUrl = REDIRECT_URL;

        if (loginStatus == LoginStatus.LOGIN) {
            log.info("토큰과 함께 홈으로 리다이렉트");
            String accessToken = this.tokenProvider.createAccessToken(user);
            String refreshToken = this.tokenProvider.createRefreshToken(user);

            redirectUrl = UriComponentsBuilder.fromUriString(REDIRECT_URL)
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build()
                .toUriString();

            Cookie cookie = new Cookie("refreshToken", refreshToken);
            cookie.setMaxAge((int) refreshExpirationTime / 1000);
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        } else if (loginStatus == LoginStatus.REGISTER) {
            log.info("회원가입 화면으로 리다이렉트");
            redirectUrl = UriComponentsBuilder.fromUriString(REDIRECT_URL + "/signup")
                .queryParam("social-email", principalDetails.getSocialEmail())
                .build()
                .toUriString();
        } else {
            redirectUrl += "/my-page/profile";
            log.info("연동된 경우니 마이페이지로 리다이렉트");
        }

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
