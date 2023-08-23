package peer.backend.service.team;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 유저 아이디 입니다."));
        List<Team> teamList = user.getTeamUsers().stream().map(x -> x.getTeam())
            .collect(Collectors.toList());
//        List<TeamUser> teamUserList = teamUserRepository.findByUserId(userId);
//        List<Team> teamList = teamUserList.stream().map(x -> x.getTeam())
//            .collect(Collectors.toList());

        return teamList;
    }
}
