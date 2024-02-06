package peer.backend.oauth;

import java.io.IOException;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuthAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Value("${url.dev-domain-url}")
    private String REDIRECT_URL;
    private static final String ERROR_URL = "/login/forbidden";

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException exception) throws IOException, ServletException {
        log.error(exception.getMessage());
        log.error(Arrays.toString(exception.getStackTrace()));

        String redirectUrl = UriComponentsBuilder.fromUriString(REDIRECT_URL + ERROR_URL)
            .queryParam("code", "failed")
            .build()
            .toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
