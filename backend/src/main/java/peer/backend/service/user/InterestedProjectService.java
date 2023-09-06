package peer.backend.service.user;

import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import peer.backend.entity.team.Team;
import peer.backend.entity.user.InterestedProject;
import peer.backend.entity.user.User;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.team.TeamRepository;
import peer.backend.repository.user.InterestedProjectRepository;
import peer.backend.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
public class InterestedProjectService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final InterestedProjectRepository interestedProjectRepository;

    @Transactional
    public InterestedProject addInterestedProject(Long userId, Long teamId) {
        User user = this.userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 유저의 아이디 입니다!"));
        Team team = this.teamRepository.findById(teamId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 팀의 아이디 입니다!"));
        InterestedProject interestedProject = InterestedProject.builder()
            .user(user)
            .team(team)
            .userId(user.getId())
            .teamId(team.getId())
            .build();
        return this.interestedProjectRepository.save(interestedProject);
    }

    @Transactional
    public List<Team> getInterestedProjectList(Long userId) {
        User user = this.userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 유저의 아이디 입니다!"));

        return user.getInterestedProjects().stream().map(InterestedProject::getTeam).collect(
            Collectors.toList());
    }

    @Transactional
    public void deleteInterestedProject(Long userId, Long teamId) {
        this.interestedProjectRepository.deleteByUserIdAndTeamId(userId, teamId);
    }

}
