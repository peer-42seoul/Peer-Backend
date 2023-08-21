package peer.backend.service.team;

import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.TeamUser;
import peer.backend.repository.team.TeamRepository;
import peer.backend.repository.team.TeamUserRepository;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;

    private final TeamUserRepository teamUserRepository;

    @Transactional
    public List<Team> getTeamList(Long userId) {
        List<TeamUser> teamUserList = teamUserRepository.findByUserId(userId);
        List<Team> teamList = teamUserList.stream().map(x -> x.getTeam())
            .collect(Collectors.toList());

        return teamList;
    }
}
