package peer.backend.service.socket;

import com.corundumstudio.socketio.SocketIOClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import peer.backend.config.jwt.TokenProvider;
import peer.backend.dto.socket.whoURDTO;
import peer.backend.dto.socket.yesWhoUAreDTO;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.user.User;
import peer.backend.repository.team.TeamRepository;
import peer.backend.repository.team.TeamUserRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class SocketServerService {

    private final RedisTemplate<String, String> redisTemplate;
    private final TeamRepository teamRepository;
    private final TeamUserRepository teamUserRepository;

    @Autowired
    private TokenProvider tokenProvider;

    public boolean IsOnline(User user) {
        if (redisTemplate.opsForValue().get("onlineStatus:" + user.getId()) != null)
            return true;
        return false;
    }

    public boolean checkValidationWithToken(SocketIOClient client, String token) {
        if (tokenProvider.validateToken(token))
            return true;
        log.info("Wrong Token! Connection is closed!");
        client.disconnect();
        return false;
    }

    public User getUserWithToken(String token) {
        Authentication jwt = tokenProvider.getAuthentication(token);
        return User.authenticationToUser(jwt);
    }

    public yesWhoUAreDTO makeUserInfo(User target, whoURDTO data) {
        yesWhoUAreDTO result;
        if (data.getTeamId() == null && data.getTeamName() == null) {
            result = yesWhoUAreDTO.builder()
                    .userId(target.getId().toString())
                    .teamId(null)
                    .teamName(null)
                    .yourRole(null)
                    .build();
        }
        else if (data.getTeamId() == null) {
            log.info("Wrong api request, Connection is closed");
            return null;
        }
        else if (data.getTeamName() == null) {
            log.info("Wrong api request, Connection is closed");
            return null;
        }
        else {
            Long teamId = Long.parseLong(data.getTeamId());
            String teamName = data.getTeamName();
            Team teamData = teamRepository.findById(teamId)
                    .orElseThrow(() -> new NoSuchElementException("존재하지 않는 팀 입니다."));
            if (!teamData.getName().equals(teamName)) {
                return null;
            }
            TeamUser user = teamData
                    .getTeamUsers()
                    .stream()
                    .filter((member -> member.getId().equals(target.getId())))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("멤버로 존재하지 않습니다."));
            result = yesWhoUAreDTO.builder()
                    .userId(target.getId().toString())
                    .teamId(teamData.getId().toString())
                    .teamName(teamData.getName())
                    .yourRole(user.getRole())
                    .build();
        }
        return result;
    }
}
