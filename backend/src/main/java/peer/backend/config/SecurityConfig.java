package peer.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests()
                .antMatchers("/").permitAll()
                .anyRequest().permitAll()
                .and()
                .csrf().disable()
        ;
        return http.build();
    }
    @Bean
    public BCryptPasswordEncoder endcoder() {
        return new BCryptPasswordEncoder();
    }
}
