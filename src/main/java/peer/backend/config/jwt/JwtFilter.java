package peer.backend.config.jwt;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import peer.backend.exception.UnauthorizedException;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String token;
        // TODO: 일반 JwtFilter와 어드민 JwtFilter를 나누도록 리팩토링 하는게 좋을 것 같음.
        if (requestURI.startsWith("/api/v1/admin")) {
            token = this.tokenProvider.resolveAdminToken(request);
        } else {
            token = this.tokenProvider.resolveAccessToken(request);
        }
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }
        if (tokenProvider.validateToken(token)) {
            Authentication auth = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
            filterChain.doFilter(request, response);
            return;
        } else {
            throw new UnauthorizedException("유효하지 않은 토큰입니다.");
        }
    }
}
