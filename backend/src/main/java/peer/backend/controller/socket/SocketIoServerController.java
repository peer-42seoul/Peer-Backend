package peer.backend.controller.socket;

import antlr.Token;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Instanceof;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import peer.backend.config.SocketIoConfig;
import peer.backend.config.jwt.TokenProvider;
import peer.backend.entity.user.User;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequestMapping("/")
public class SocketIoServerController {
    private final SocketIOServer server;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    public SocketIoServerController(SocketIoConfig config) {
        this.server = config.socketIOServer();
        this.server.start();
        this.server.addConnectListener(onUserConnectWithSocket);
        this.server.addDisconnectListener(onUserDisconnectWithSocket);

    }

    public ConnectListener onUserConnectWithSocket = new ConnectListener() {
        @Override
        public void onConnect(SocketIOClient client) {
            log.info("Socket is Connected : " + client.getSessionId());
            List<String> token = client.getHandshakeData().getUrlParams().get("token");
            log.info("Socket Token is Connected : " + client.getHandshakeData().getUrlParams().get("token"));
            log.info("Token is valid");
            Authentication userJwt = tokenProvider.getAuthentication(token.get(0));
            User user = User.authenticationToUser(userJwt);
            log.info(user.getName() + "is Online Status");
        }
    };


    public DisconnectListener onUserDisconnectWithSocket = new DisconnectListener() {
        @Override
        public void onDisconnect(SocketIOClient client) {
            log.info("Socket is disconnected : " + client.getSessionId());
        }
    };
}
