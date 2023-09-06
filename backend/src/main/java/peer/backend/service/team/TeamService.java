package peer.backend.service.team;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import peer.backend.dto.team.UpdateTeamRequest;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.user.User;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.team.TeamRepository;
import peer.backend.repository.team.TeamUserRepository;
import peer.backend.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final UserRepository userRepository;

    private final TeamRepository teamRepository;

    private final TeamUserRepository teamUserRepository;

    @Transactional
    public List<Team> getTeamList(Long userId) {
        User user = this.userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 유저 아이디 입니다."));

        return user.getTeamUsers().stream().map(TeamUser::getTeam)
            .collect(Collectors.toList());
    }

    @Transactional
    public Team getTeamById(Long teamId) {
        return this.teamRepository.findById(teamId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 팀 아이디 입니다."));
    }

    @Transactional
    public Team getTeamByName(String teamName) {
        return this.teamRepository.findByName(teamName)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 팀 아이디 입니다."));
    }

    @Transactional
    public void updateTeam(Long teamId, UpdateTeamRequest request) {
        Team team = this.getTeamById(teamId);
        team.update(request);
    }

    @Transactional
    public void deleteTeamUser(Long teamId, Long userId) {
        if (!this.teamUserRepository.existsByUserIdAndTeamId(userId, teamId)) {
            throw new NotFoundException("해당 유저는 팀원이 아닙니다!");
        }
        this.teamUserRepository.deleteByUserIdAndTeamId(userId, teamId);
    }
}
