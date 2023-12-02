package peer.backend.service.socket;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import peer.backend.entity.user.User;

@Service
@RequiredArgsConstructor
public class SocketServerService {

    private final RedisTemplate<String, String> redisTemplate;

    public boolean IsOnline(User user) {
        if (redisTemplate.opsForValue().get("onlineStatus:" + user.getId()) != null)
            return true;
        return false;
    }
}
