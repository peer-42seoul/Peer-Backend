package peer.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import peer.backend.config.jwt.ExceptionHandlerFilter;
import peer.backend.config.jwt.JwtFilter;
import peer.backend.config.jwt.TokenProvider;
import peer.backend.exception.UnauthorizedException;
@RequiredArgsConstructor
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final TokenProvider tokenProvider;
    @Override
    public void configure(HttpSecurity builder) throws UnauthorizedException {
        builder.addFilterBefore(new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);
        builder.addFilterBefore(new ExceptionHandlerFilter(), JwtFilter.class);
    }
}
