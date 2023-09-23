package peer.backend.oauth;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import peer.backend.config.jwt.TokenProvider;
import peer.backend.entity.user.User;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuthAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final String REDIRECT_URL = "http://localhost:8080";

    private final TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
        PrincipalDetails principalDetails =  (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();
        String redirectUrl = REDIRECT_URL;

        if (!principalDetails.isRegistered()) {
            log.info("회원가입 화면으로 리다이렉트");
            redirectUrl += "/register";
        } else {
            log.info("토큰과 함께 홈으로 리다이렉트");
            String accessToken = this.tokenProvider.createAccessToken(user);
            String refreshToken = this.tokenProvider.createRefreshToken(user);

            redirectUrl = UriComponentsBuilder.fromUriString(REDIRECT_URL)
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build()
                .toUriString();
        }

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
