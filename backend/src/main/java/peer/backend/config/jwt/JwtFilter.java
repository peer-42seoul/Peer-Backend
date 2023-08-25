package peer.backend.config.jwt;


import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import peer.backend.exception.NotFoundException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        String requestUri = request.getRequestURI();

        if (!requestUri.startsWith("/membership") && !requestUri.startsWith("/login")){
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                logger.info("토큰이 없습니다");
                throw new NotFoundException("토큰이 없습니다.");
    //            return;
            }
            String token = authorizationHeader.substring(7);
            // 토큰 검증
            if (TokenProvider.isExpired(token, secretKey)) {
                logger.info("토큰이 만료되었습니다");
                filterChain.doFilter(request, response);
                return;
            }
        }
        //UserName Token에서 꺼내기
        String userName = "";

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userName, null, List.of(new SimpleGrantedAuthority("USER")));

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}
