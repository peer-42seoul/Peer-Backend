package peer.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import peer.backend.annotation.NoLogging;
import peer.backend.dto.socket.ChatDTO;
import peer.backend.entity.chat.Chat;
import peer.backend.service.ChatService;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @NoLogging
    @SendTo("/topic/team/{teamId}")
    @MessageMapping("/send/{teamId}") //여기로 전송되면 메서드 호출 -> WebSocketConfig prefixes 에서 적용한건 앞에 생략
    //구독하고 있는 장소로 메시지 전송 (목적지)  -> WebSocketConfig Broker 에서 적용한건 앞에 붙어줘야됨
    public ChatDTO chat(@DestinationVariable Long teamId, ChatDTO chatDTO) {
        System.out.println(teamId);
        //채팅 저장
        Chat chat = chatService.createChat(chatDTO.getUserId(), chatDTO.getUserName(),
            chatDTO.getTeamId(),
            chatDTO.getMessage(), chatDTO.getDate());
        return chatDTO;
    }
}
