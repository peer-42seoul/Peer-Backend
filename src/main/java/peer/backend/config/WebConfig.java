package peer.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import peer.backend.config.team.StringToTeamStatusConverter;
import peer.backend.config.team.StringToTeamUserRoleTypeConverter;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        try {
            registry.addConverter(new StringToTeamStatusConverter());
            registry.addConverter(new StringToTeamUserRoleTypeConverter());
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
