package peer.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsUtils;
import peer.backend.config.jwt.JwtAccessDeniedHandler;
import peer.backend.config.jwt.JwtAuthenticationEntryPoint;
import peer.backend.config.jwt.JwtFilter;
import peer.backend.config.jwt.TokenProvider;
import peer.backend.oauth.OAuthAuthenticationSuccessHandler;
import peer.backend.oauth.PrincipalOauth2UserService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfig corsConfig;
    private final TokenProvider tokenProvider;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final PrincipalOauth2UserService principalOauth2UserService;
    private final OAuthAuthenticationSuccessHandler oAuthAuthenticationSuccessHandler;

    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
//            .addFilter(corsConfig.corsFilter())
            .cors()
            .and()
            .addFilterBefore(new JwtFilter(tokenProvider), OAuth2LoginAuthenticationFilter.class)
            .httpBasic().disable()
            .csrf().disable()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

            .and()
            .authorizeRequests()
            .antMatchers("/admin/**").hasRole("ADMIN")
            .antMatchers("/api/v1/signin/**", "/api/v1/signup/**", "/access-token", "/", "/error")
            .permitAll()
            .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
            .anyRequest().authenticated()

            .and()
            .exceptionHandling()
            .accessDeniedHandler(jwtAccessDeniedHandler)
            .authenticationEntryPoint(jwtAuthenticationEntryPoint)

            .and()
            .apply(new JwtSecurityConfig(tokenProvider))

            .and()
            .oauth2Login()
            .successHandler(oAuthAuthenticationSuccessHandler)
            .userInfoEndpoint()
            .userService(principalOauth2UserService);
        return httpSecurity.build();
    }
}
