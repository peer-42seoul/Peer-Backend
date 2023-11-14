package peer.backend.config;


import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

@Configuration
@CrossOrigin
public class SocketIoConfig {

    @Value("${socket.io.port}")
    private int socketIoPort;

    @Value("${socket.io.host}")
    private String socketIoHost;

    private SocketIOServer server;

    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(socketIoHost); // 호스트 설정
        config.setPort(socketIoPort); // 포트 설정
        SocketConfig portConfig = new SocketConfig();
        portConfig.setReuseAddress(true);
        config.setSocketConfig(portConfig);
        this.server = new SocketIOServer(config);
        return server;
    }
}
