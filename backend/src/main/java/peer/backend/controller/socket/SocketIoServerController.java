package peer.backend.controller.socket;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import peer.backend.config.SocketIoConfig;
import peer.backend.config.jwt.TokenProvider;
import peer.backend.entity.user.User;
import peer.backend.repository.user.UserRepository;
import peer.backend.service.socket.SocketServerService;

import java.util.List;
import java.util.NoSuchElementException;

import peer.backend.dto.socket.tempDTO;

@Component
@Slf4j
@RequestMapping("/")
public class SocketIoServerController {
    private final SocketIOServer server;
    private final SocketServerService socketServerService;
    private final UserRepository userRepository;

    @Autowired
    private TokenProvider tokenProvider;

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public SocketIoServerController(SocketIoConfig config, SocketServerService socketServerService, UserRepository userRepository, RedisTemplate<String, String> redisTemplate) {
        this.server = config.socketIOServer();
        this.socketServerService = socketServerService;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        this.server.start();
        this.server.addConnectListener(onUserConnectWithSocket);
        this.server.addDisconnectListener(onUserDisconnectWithSocket);

        this.server.addEventListener("checkOnline", tempDTO.class, IsHeOnline);

    }

    public ConnectListener onUserConnectWithSocket = new ConnectListener() {
        @Override
        public void onConnect(SocketIOClient client) {
            log.info("Socket is Connected : " + client.getSessionId());
            List<String> token = client.getHandshakeData().getUrlParams().get("token");
            if (!tokenProvider.validateToken2(token.get(0))) {
                log.info("Wrong Token! Connection is closed!");
                client.disconnect();
            }
            Authentication userJwt = tokenProvider.getAuthentication(token.get(0));
            User user = User.authenticationToUser(userJwt);
            log.info(user.getName() + " is Online Status");
            redisTemplate.opsForValue().set("onlineStatus:" + user.getId(), client.getSessionId().toString());
        }
    };


    public DisconnectListener onUserDisconnectWithSocket = new DisconnectListener() {
        @Override
        public void onDisconnect(SocketIOClient client) {
            List<String> token = client.getHandshakeData().getUrlParams().get("token");
            Authentication userJwt = tokenProvider.getAuthentication(token.get(0));
            User user = User.authenticationToUser(userJwt);
            redisTemplate.delete("onlineStatus:" + user.getId());
            log.info(user.getName() + " is offline Status");
            log.info("Socket is disconnected : " + client.getSessionId());
        }
    };

    public DataListener<tempDTO> IsHeOnline = new DataListener<>() {
        @Override
        public void onData(SocketIOClient client, tempDTO data, AckRequest ackSender) throws Exception {
            Long userId;
            User user;
            try {
                userId = Long.parseLong(data.getData());
                user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User doesn't exist!"));
            } catch (NumberFormatException e) {
                client.sendEvent("checkOnline", "Can not find user!");
                return ;
            } catch (NoSuchElementException e) {
                client.sendEvent("checkOnline", e.getMessage());
                return ;
            }
            if(socketServerService.IsOnline(user)) {
                log.info(user.getNickname() + " is Online!");
                client.sendEvent("checkOnline", user.getNickname() + " is Online!");
            }else{
                log.info(user.getNickname() + " is offline!");
                client.sendEvent("checkOnline", user.getNickname() + " is offline!");
            }
        }
    };
}
