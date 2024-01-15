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
import peer.backend.dto.socket.whoURDTO;
import peer.backend.dto.socket.yesWhoUAreDTO;
import peer.backend.entity.user.User;
import peer.backend.repository.team.TeamRepository;
import peer.backend.repository.team.TeamUserRepository;
import peer.backend.repository.user.UserRepository;
import peer.backend.service.socket.SocketServerService;

import java.util.EventListener;
import java.util.List;
import java.util.NoSuchElementException;

import peer.backend.dto.socket.tempDTO;

@Component
@Slf4j
@RequestMapping("/")
public class SocketIoServerController {
    private final SocketServerService socketServerService;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final TokenProvider tokenProvider;

    @Autowired
    public SocketIoServerController(SocketIoConfig config,
                                    SocketServerService socketServerService,
                                    UserRepository userRepository,
                                    TokenProvider tokenProvider,
                                    RedisTemplate<String, String> redisTemplate) {
        SocketIOServer server = config.socketIOServer();
        this.socketServerService = socketServerService;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        this.tokenProvider = tokenProvider;
        server.start();
        server.addConnectListener(onUserConnectWithSocket);
        server.addDisconnectListener(onUserDisconnectWithSocket);

        server.addEventListener("checkOnline", tempDTO.class, isHeOnline);
        server.addEventListener("whoAmI", whoURDTO.class, whoAmI);

    }

    public ConnectListener onUserConnectWithSocket = new ConnectListener() {
        @Override
        public void onConnect(SocketIOClient client) {
            log.info("Socket is Connected : " + client.getSessionId());
            String token = client.getHandshakeData().getUrlParams().get("token").get(0);

            if (!socketServerService.checkValidationWithToken(client, token))
                return;
            User user = tokenProvider.getUserWithToken(token);

            log.info(user.getName() + " is Online Status");
            redisTemplate.opsForValue().set("onlineStatus:" + user.getId(), client.getSessionId().toString());
        }
    };


    public DisconnectListener onUserDisconnectWithSocket = new DisconnectListener() {
        @Override
        public void onDisconnect(SocketIOClient client) {
            String token = client.getHandshakeData().getUrlParams().get("token").get(0);
            User user = tokenProvider.getUserWithToken(token);
            redisTemplate.delete("onlineStatus:" + user.getId());
            log.info(user.getName() + " is offline Status");
            log.info("Socket is disconnected : " + client.getSessionId());
        }
    };

    public DataListener<tempDTO> isHeOnline = new DataListener<>() {
        @Override
        public void onData(SocketIOClient client, tempDTO data, AckRequest ackSender) throws Exception {
            long userId;
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

    public DataListener<whoURDTO> whoAmI = new DataListener<>() {
        @Override
        public void onData(SocketIOClient client, whoURDTO data, AckRequest ackSender){
            String token = client.getHandshakeData().getUrlParams().get("token").get(0);
            if (!socketServerService.checkValidationWithToken(client, token))
                return ;
            User target = tokenProvider.getUserWithToken(token);
            yesWhoUAreDTO result = null;
            try {
               result = socketServerService.makeUserInfo(target, data);
            } catch (Exception e) {
                client.sendEvent("whoAmI", e.getMessage());
                client.disconnect();
                return;
            }
            if (result == null) {
                client.sendEvent("whoAmI", "잘못된 요청입니다.");
                client.disconnect();
                return;
            }
            ackSender.sendAckData(result);
        }
    };
}
