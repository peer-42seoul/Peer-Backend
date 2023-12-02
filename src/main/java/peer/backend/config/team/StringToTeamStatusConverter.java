package peer.backend.config.team;

import org.springframework.core.convert.converter.Converter;
import peer.backend.entity.team.enums.TeamStatus;
import peer.backend.exception.ConversionFailedException;

public class StringToTeamStatusConverter implements Converter<String, TeamStatus> {
    @Override
    public TeamStatus convert(String source) {
        try {
            return TeamStatus.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
             return null;
        }
    }
}
