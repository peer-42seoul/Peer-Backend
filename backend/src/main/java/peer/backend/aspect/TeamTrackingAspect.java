package peer.backend.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import peer.backend.entity.team.Team;
import peer.backend.mongo.entity.TeamTracking;
import peer.backend.mongo.repository.TeamTrackingRepository;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class TeamTrackingAspect {

    private final TeamTrackingRepository teamTrackingRepository;

    @Pointcut("@annotation(peer.backend.annotation.tracking.TeamCreateTracking)")
    public void teamCreate() {
    }

    @AfterReturning(pointcut = "peer.backend.aspect.TeamTrackingAspect.teamCreate()", returning = "team")
    public void teamCreateTracking(Team team) {
        TeamTracking teamTracking = TeamTracking.builder()
            .teamId(team.getId())
            .teamName(team.getName())
            .actionDate(team.getCreatedAt().toLocalDate())
            .actionType(team.getType())
            .teamStatus(team.getStatus())
            .build();
        this.teamTrackingRepository.save(teamTracking);
    }
}
