package peer.backend.config.team;

import org.springframework.core.convert.converter.Converter;
import peer.backend.entity.team.enums.TeamUserRoleType;

public class StringToTeamUserRoleTypeConverter implements Converter<String, TeamUserRoleType> {
    @Override
    public TeamUserRoleType convert(String source) {
        return TeamUserRoleType.valueOf(source.toUpperCase());
    }
}
