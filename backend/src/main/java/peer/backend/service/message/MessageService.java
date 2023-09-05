package peer.backend.service.message;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import peer.backend.dto.MessageUserDTO;
import peer.backend.entity.message.Message;
import peer.backend.entity.user.User;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.message.MessageRepository;
import peer.backend.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
@EnableWebMvc
public class MessageService {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    // Todo: 해당 유저의 쪽지목록들 가져오기, userId -> Principle로 변경
    @Transactional(readOnly = true)
    public List<MessageUserDTO> myMessageList(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 유저의 아이디 입니다!"));

        // 현재 사용자와 관련된 메시지를 모두 조회합니다.
        List<Message> relatedMessages = messageRepository.findBySenderOrReceiver(user, user);

        // 관련된 메시지에서 상대방의 User 객체를 추출하여 중복을 제거한 목록을 생성합니다.
        List<MessageUserDTO> contactList = relatedMessages.stream()
            .filter(message -> !message.getSender().equals(message.getReceiver())) // 자기 자신은 추가하지 않음
            .map(message -> {
                User otherUser;
                if (message.getSender().equals(user)) {
                    otherUser = message.getReceiver();
                } else {
                    otherUser = message.getSender();
                }
                // 상대방(User)의 프로필 이미지와 닉네임을 가져와서 MessageUserDTO로 생성
                String profileImage = otherUser.getImageUrl();
                String nickName = otherUser.getNickname();
                return MessageUserDTO.builder()
                    .profileImage(profileImage)
                    .nickName(nickName)
                    .build();
            })
            .distinct()
            .collect(Collectors.toList());

        // Todo: 보낸순으로 내림차순 정

        return contactList;
    }


    // Todo: 해당 유저가 어떤 유저와 나눈 쪽지 가져오기
    // Todo: 상대 프로필을 통해 쪽지 보내기
}
