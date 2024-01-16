package peer.backend.service;

import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import peer.backend.entity.team.TeamUser;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.team.TeamUserRepository;

@Service
@RequiredArgsConstructor
public class TeamUserService {

    private final TeamUserRepository teamUserRepository;

    @Transactional
    public void deleteTeamUser(Long userId, Long teamId) {
        TeamUser teamUser = this.getTeamUser(userId, teamId);
        this.teamUserRepository.delete(teamUser);
    }

    @Transactional
    public TeamUser getTeamUser(Long userId, Long teamId) {
        return this.teamUserRepository.findByUserIdAndTeamId(userId, teamId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 TeamUser 입니다!"));
    }
}
