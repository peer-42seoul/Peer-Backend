package peer.backend.service.dnd;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.dto.dnd.RequestDnDDTO;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.user.User;
import peer.backend.mongo.entity.TeamDnD;
import peer.backend.mongo.repository.PeerLogDnDRepository;
import peer.backend.mongo.repository.TeamDnDRepository;
import peer.backend.repository.team.TeamRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DnDService {

    private final TeamDnDRepository teamDnDRepository;
    private final PeerLogDnDRepository peerLogDnDRepository;
    private final TeamRepository teamRepository;

    private static final String DND_MAIN_IDENTIFIER = "dnd-sub";

    private RedisTemplate<String, List<TeamUser>> redisTemplate;


    public boolean checkValidMemberFromTeam(Long teamId, User requester) {
        List<TeamUser> members = this.getTeamUserInDB(teamId);
        return members.stream().noneMatch(member -> member.getUserId().equals(requester.getId()));
    }

    private List<TeamUser> getTeamUserInRedis(Long teamId) {
        return this.redisTemplate.opsForValue().get(Long.toString(teamId) + "-" + DND_MAIN_IDENTIFIER);
    }

    private void saveTeamUserToRedis(List<TeamUser> members, Long teamId) {
        redisTemplate.opsForValue().set(Long.toString(teamId) + "-" + DND_MAIN_IDENTIFIER, members);
    }

    private List<TeamUser> getTeamUserInDB(Long teamId) {
        List<TeamUser> userList = this.getTeamUserInRedis(teamId);
        if (userList == null) {
            userList = teamRepository.
                    findById(teamId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 팀입니다."))
                    .getTeamUsers();
            this.saveTeamUserToRedis(userList, teamId);
        }

        return userList;
    }

    @Transactional
    public TeamDnD createDnD(TeamDnD data) throws RuntimeException {
        Optional<Team> target = teamRepository.findById(data.getTeamId());
        if (target.isEmpty()) {
            throw new NoSuchElementException("Plz, check, There is no team id");
        }
        TeamDnD saveData = TeamDnD.builder()
                .teamId(data.getTeamId())
                .type(data.getType())
                .widgets(data.getWidgets())
                .build();

        TeamDnD ret;
        try {
            if (data.getType().equals("team"))
                ret = this.teamDnDRepository.save(saveData);
            else
                ret = this.peerLogDnDRepository.save(saveData);
        } catch (Exception e) {
            throw new RuntimeException("DB Server makes an error.");
        }
        return ret;
    }

    @Transactional(readOnly = true)
    public TeamDnD getDnD(RequestDnDDTO data) throws NoSuchElementException {
        TeamDnD ret;
        if (data.getType().equals("team")) {
            ret = this.teamDnDRepository.findByTeamId(data.getTeamId());
        }
        else {
            ret = this.peerLogDnDRepository.findByTeamId(data.getTeamId());
        }
        return ret;
    }

    @Transactional
    public TeamDnD updateDnD(TeamDnD data) throws Exception {
        TeamDnD example = new TeamDnD();
        TeamDnD ret;
        example.setTeamId(data.getTeamId());
        try {
            if (data.getType().equals("team")) {
                if (!this.teamDnDRepository.exists(Example.of(example))){
                    throw new NoSuchElementException("There is no that Dnd File.");
                }
                example = this.teamDnDRepository.findByTeamId(data.getTeamId());
                example.setWidgets(data.getWidgets());
                ret = this.teamDnDRepository.save(example);
            } else {
                if (!this.peerLogDnDRepository.exists(Example.of(example))){
                    throw new NoSuchElementException("There is no that Dnd File.");
                }
                example = this.peerLogDnDRepository.findByTeamId(data.getTeamId());
                example.setWidgets(data.getWidgets());
                ret = this.peerLogDnDRepository.save(data);
            }
        } catch (Exception e) {
            throw new Exception("DnD file update failed!");
        }
        return ret;
    }

    @Transactional
    public void deleteDnD(RequestDnDDTO data) throws RuntimeException {
        TeamDnD example = new TeamDnD();
        example.setTeamId(data.getTeamId());
        try {
            if (data.getType().equals("team")) {
                if (!this.teamDnDRepository.exists(Example.of(example))){
                    throw new NoSuchElementException("There is no that Dnd File.");
                }
                this.teamDnDRepository.deleteByTeamId(data.getTeamId());
            }
            else {
                if (!this.peerLogDnDRepository.exists(Example.of(example))){
                    throw new NoSuchElementException("There is no that Dnd File.");
                }
                this.peerLogDnDRepository.deleteByTeamId(data.getTeamId());
            }
        } catch (Exception e) {
            throw new RuntimeException("DnD file delete is failed!");
        }

    }
}
