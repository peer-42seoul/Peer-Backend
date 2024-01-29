package peer.backend.aspect;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import peer.backend.dto.team.TeamSettingInfoDto;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.enums.TeamStatus;
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

    @Pointcut("@annotation(peer.backend.annotation.tracking.TeamUpdateTracking)")
    public void teamUpdate() {
    }

    @Pointcut("@annotation(peer.backend.annotation.tracking.DisperseTeamTracking)")
    public void disperseTeam() {
    }

    @Pointcut("@annotation(peer.backend.annotation.tracking.CompleteTeamTracking)")
    public void completeTeam() {
    }

    @AfterReturning(pointcut = "peer.backend.aspect.TeamTrackingAspect.teamCreate()", returning = "team")
    public void teamCreateTracking(Team team) {
        TeamTracking teamTracking = TeamTracking.builder()
            ._id(team.getId())
            .teamId(team.getId())
            .teamName(team.getName())
            .actionDate(team.getCreatedAt().toLocalDate())
            .actionType(team.getType())
            .teamStatus(team.getStatus())
            .build();
        this.teamTrackingRepository.save(teamTracking);
    }

    @Around("peer.backend.aspect.TeamTrackingAspect.teamUpdate()")
    public Object teamUpdateTracking(ProceedingJoinPoint pjp) throws Throwable {
        final Object[] arguments = pjp.getArgs();
        Long teamId = (Long) arguments[0];
        TeamSettingInfoDto teamSettingInfoDto = (TeamSettingInfoDto) arguments[1];

        Object result = pjp.proceed();

        TeamTracking teamTracking = this.teamTrackingRepository.findByTeamId(teamId);
        teamTracking.setTeamName(teamSettingInfoDto.getName());
        teamTracking.setActionType(teamSettingInfoDto.getType());
        teamTracking.setTeamStatus(teamSettingInfoDto.getStatus());
        if (teamTracking.getTeamStatus().equals(TeamStatus.COMPLETE)) {
            teamTracking.setActionFinishedDate(LocalDate.now());
        }
        this.teamTrackingRepository.save(teamTracking);

        return result;
    }

    @AfterReturning(pointcut = "peer.backend.aspect.TeamTrackingAspect.disperseTeam()", returning = "team")
    public void disperseTeamTracking(Team team) {
        TeamTracking teamTracking = this.teamTrackingRepository.findByTeamId(team.getId());
        teamTracking.setTeamStatus(TeamStatus.DISPERSE);
        this.teamTrackingRepository.save(teamTracking);
    }

    @AfterReturning(pointcut = "peer.backend.aspect.TeamTrackingAspect.completeTeam()", returning = "team")
    public void completeTeamTracking(Team team) {
        TeamTracking teamTracking = this.teamTrackingRepository.findByTeamId(team.getId());
        teamTracking.setTeamStatus(TeamStatus.COMPLETE);
        this.teamTrackingRepository.save(teamTracking);
    }
}
